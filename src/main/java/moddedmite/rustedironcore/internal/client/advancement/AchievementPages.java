package moddedmite.rustedironcore.internal.client.advancement;

import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementProgress;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.client.api.hud.HudGraphics;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.network.packets.C2SRequestAdvancements;
import moddedmite.rustedironcore.network.Network;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Block;
import net.minecraft.GuiButton;
import net.minecraft.I18n;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.minecraft.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class AchievementPages {

    private static final ResourceLocation FRAME = new ResourceLocation("minecraft", "textures/gui/achievement/achievement_background.png");
    private static final ResourceLocation TABS = new ResourceLocation("minecraft", "textures/gui/container/creative_inventory/tabs.png");
    private static final int TAB_WIDTH = 28;
    private final HudGraphics graphics;
    private final ItemStack vanillaIcon = new ItemStack(Block.workbench);
    private final Map<String, ItemStack> icons = new HashMap<>();
    private final List<Advancement> roots = new ArrayList<>();
    private final Set<String> completedBranches = new HashSet<>();
    private Map<String, AdvancementNode> nodes = Map.of();
    private Advancement selected;
    private ResourceLocation background;
    private GuiButton previousPage, nextPage;
    private int left, top, frameWidth, frameHeight;
    private int page, dragX, dragY;
    private double scrollX, scrollY;
    private double zoom = 1;
    private final AdvancementTransition zoomTransition = new AdvancementTransition(1);
    private final AdvancementTransition windowSize = new AdvancementTransition(0.75);
    private double zoomAnchorX, zoomAnchorY;
    private int revision = -1;
    private boolean dragging;
    private boolean requestedSnapshot;

    public AchievementPages(Minecraft minecraft) {
        graphics = new HudGraphics(minecraft);
    }

    public void init(int left, int top, int frameWidth, int frameHeight, List<GuiButton> buttons) {
        if (this.frameWidth > 0) {
            // Keep the viewed tree center when the GUI resolution changes.
            scrollX += left + frameWidth / 2.0 - (this.left + this.frameWidth / 2.0);
            scrollY += top + frameHeight / 2.0 - (this.top + this.frameHeight / 2.0);
        }
        previousPage = new GuiButton(0x524943, left - 20, top - 23, 18, 20, "<");
        nextPage = new GuiButton(0x524944, left + frameWidth + 2, top - 23, 18, 20, ">");
        buttons.add(previousPage);
        buttons.add(nextPage);
        resize(left, top, frameWidth, frameHeight);
        refresh();
        if (ClientAdvancements.isAwaitingSnapshot() && !requestedSnapshot) {
            requestedSnapshot = true;
            Network.sendToServer(new C2SRequestAdvancements());
        }
    }

    public boolean isVanillaPage() {
        return selected == null;
    }

    public double windowSize() {
        return windowSize.value();
    }

    public void adjustWindowSize(int amount) {
        windowSize.animateTo(Math.max(0, Math.min(1, windowSize.target() + Integer.signum(amount) * 0.125)));
    }

    public void updateWindowSize(long now) {
        windowSize.update(now);
    }

    public void updateZoom(long now) {
        double nextZoom = zoomTransition.update(now);
        if (nextZoom == zoom) return;
        double x = zoomAnchorX * contentWidth(), y = zoomAnchorY * contentHeight();
        double ratio = nextZoom / zoom;
        scrollX = x - (x - scrollX) * ratio;
        scrollY = y - (y - scrollY) * ratio;
        zoom = nextZoom;
    }

    public void resize(int left, int top, int frameWidth, int frameHeight) {
        if (this.frameWidth > 0) {
            scrollX += this.left - left;
            scrollY += this.top - top;
        }
        this.left = left;
        this.top = top;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        previousPage.xPosition = left - 20;
        previousPage.yPosition = nextPage.yPosition = top - 23;
        nextPage.xPosition = left + frameWidth + 2;
    }

    public int contentWidth() {
        return frameWidth - 32;
    }

    public int contentHeight() {
        return frameHeight - 47;
    }

    public int contentLeft() {
        return left + 16;
    }

    public int contentTop() {
        return top + 17;
    }

    public void refresh() {
        if (revision != ClientAdvancements.revision()) {
            revision = ClientAdvancements.revision();
            icons.clear();
            roots.clear();
            ClientAdvancements.DEFINITIONS.values().stream()
                    .filter(advancement -> advancement.parent() == null && advancement.display() != null).forEach(roots::add);
            if (selected != null) {
                Advancement updated = ClientAdvancements.DEFINITIONS.get(selected.id().toString());
                if (updated == null || updated.parent() != null || updated.display() == null) select(null);
                else {
                    selected = updated;
                    nodes = new AdvancementLayout().build(selected);
                    refreshVisibility();
                }
            }
        }
        page = Math.min(page, lastPage());
        previousPage.drawButton = nextPage.drawButton = lastPage() > 0;
        previousPage.enabled = page > 0;
        nextPage.enabled = page < lastPage();
    }

    private int tabsPerPage() {
        return Math.max(1, frameWidth / TAB_WIDTH);
    }

    private int lastPage() {
        return roots.size() / tabsPerPage();
    }

    private void select(Advancement root) {
        selected = root;
        dragging = false;
        nodes = root == null ? Map.of() : new AdvancementLayout().build(root);
        background = root != null && root.display().has("background")
                ? AdvancementBackgrounds.resolve(graphics.getMinecraft().getResourceManager(), new ResourceLocation(root.display().get("background").getAsString())) : null;
        refreshVisibility();
        zoom = 1;
        zoomTransition.reset(1);
        scrollX = 24;
        AdvancementNode node = root == null ? null : nodes.get(root.id().toString());
        scrollY = node == null ? 24 : contentHeight() / 2 - (int) (node.row() * 52);
    }

    private void refreshVisibility() {
        completedBranches.clear();
        for (AdvancementNode node : nodes.values()) {
            if (!ClientAdvancements.progress(node.advancement()).isDone(node.advancement())) continue;
            for (Advancement current = node.advancement(); current != null;) {
                if (!completedBranches.add(current.id().toString())) break;
                current = current.parent() == null ? null : ClientAdvancements.DEFINITIONS.get(current.parent().toString());
            }
        }
    }

    public boolean buttonClicked(GuiButton button) {
        if (button == previousPage) page = Math.max(0, page - 1);
        else if (button == nextPage) page = Math.min(lastPage(), page + 1);
        else return false;
        refresh();
        return true;
    }

    public boolean mouseClicked(int x, int y, int button) {
        if (button != 0) return false;
        refresh();
        if (y >= top - 28 && y < top && x >= left + 2) {
            int index = (x - left - 2) / TAB_WIDTH;
            int tab = page * tabsPerPage() + index;
            if (index < tabsPerPage() && tab <= roots.size()) {
                select(tab == 0 ? null : roots.get(tab - 1));
                return true;
            }
        }
        if (!isVanillaPage() && inside(x, y)) {
            dragging = true;
            dragX = x;
            dragY = y;
            return true;
        }
        return false;
    }

    public void mouseDragged(int x, int y) {
        if (!dragging) return;
        scrollX += x - dragX;
        scrollY += y - dragY;
        dragX = x;
        dragY = y;
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void mouseScrolled(int x, int y, int amount) {
        if (amount == 0) return;
        if (y >= top - 28 && y < top && x >= left + 2 && x < left + 2 + tabsPerPage() * TAB_WIDTH) {
            page = Math.max(0, Math.min(lastPage(), page - Integer.signum(amount)));
            refresh();
        } else if (!isVanillaPage() && inside(x, y)) {
            mouseDragged(x, y);
            zoomAnchorX = (x - contentLeft()) / (double) contentWidth();
            zoomAnchorY = (y - contentTop()) / (double) contentHeight();
            zoomTransition.animateTo(Math.max(0.5, Math.min(2, Math.round((zoomTransition.target() + Integer.signum(amount) * 0.1) * 10) / 10.0)));
        }
    }

    public boolean keyTyped(int key) {
        if (isVanillaPage()) return false;
        switch (key) {
            case 203 -> scrollX += 24;
            case 205 -> scrollX -= 24;
            case 200 -> scrollY += 24;
            case 208 -> scrollY -= 24;
            default -> { return false; }
        }
        return true;
    }

    public void renderPage() {
        graphics.begin();
        try {
            drawTree();
            drawFrame();
            graphics.drawString(TextComponentSerializer.fromJson(selected.display().get("title")), left + 15, top + 5, 0xFF404040, false);
        } finally {
            graphics.end();
        }
    }

    public void drawFrame() {
        graphics.begin();
        try {
            int[] columns = {0, 16, frameWidth - 16, frameWidth};
            int[] rows = {0, 17, frameHeight - 30, frameHeight};
            int[] u = {0, 16, 240, 256};
            int[] v = {0, 17, 172, 202};
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (row == 1 && column == 1) continue;
                    graphics.blit(FRAME, left + columns[column], top + rows[row], u[column], v[row],
                            columns[column + 1] - columns[column], rows[row + 1] - rows[row],
                            u[column + 1] - u[column], v[row + 1] - v[row], 256, 256);
                }
            }
        } finally {
            graphics.end();
        }
    }

    public void drawContentBackground() {
        graphics.begin();
        try {
            graphics.fill(contentLeft(), contentTop(), contentLeft() + contentWidth(), contentTop() + contentHeight(), 0xFF202020);
        } finally {
            graphics.end();
        }
    }

    public void renderTabs(int mouseX, int mouseY) {
        graphics.begin();
        try {
            for (int index = 0; index < tabsPerPage(); index++) {
                int tab = page * tabsPerPage() + index;
                if (tab > roots.size()) break;
                Advancement root = tab == 0 ? null : roots.get(tab - 1);
                int x = left + 2 + index * TAB_WIDTH;
                boolean active = root == selected;
                graphics.blit(TABS, x, top - 28, index == 0 ? 0 : 28, active ? 32 : 0, 28, active ? 32 : 28);
                graphics.renderItem(root == null ? vanillaIcon : icon(root), x + 6, top - 20);
            }
            if (mouseY >= top - 28 && mouseY < top && mouseX >= left + 2) {
                int index = (mouseX - left - 2) / TAB_WIDTH;
                int tab = page * tabsPerPage() + index;
                if (index < tabsPerPage() && tab <= roots.size()) {
                    String title = tab == 0 ? I18n.getString("ric.advancement.minecraft")
                            : graphics.format(TextComponentSerializer.fromJson(roots.get(tab - 1).display().get("title")));
                    tooltip(List.of(title), mouseX, mouseY);
                }
            } else if (!isVanillaPage() && inside(mouseX, mouseY)) {
                double x = (mouseX - left - 16 - scrollX) / zoom;
                double y = (mouseY - top - 17 - scrollY) / zoom;
                for (AdvancementNode node : nodes.values()) {
                    if (visible(node.advancement()) && x >= nodeX(node) && x < nodeX(node) + 26
                            && y >= nodeY(node) && y < nodeY(node) + 26) {
                        tooltip(node.advancement(), mouseX, mouseY);
                        break;
                    }
                }
            }
        } finally {
            graphics.end();
        }
    }

    public boolean inside(int x, int y) {
        return x >= contentLeft() && x < contentLeft() + contentWidth() && y >= contentTop() && y < contentTop() + contentHeight();
    }

    private void drawTree() {
        Minecraft minecraft = graphics.getMinecraft();
        int scale = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor();
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT | GL11.GL_ENABLE_BIT);
        graphics.pushPose();
        try {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(contentLeft() * scale, minecraft.displayHeight - (contentTop() + contentHeight()) * scale, contentWidth() * scale, contentHeight() * scale);
            graphics.fill(contentLeft(), contentTop(), contentLeft() + contentWidth(), contentTop() + contentHeight(), 0xFF202020);
            graphics.translate((float) (left + 16 + scrollX), (float) (top + 17 + scrollY), 0);
            graphics.scale((float) zoom, (float) zoom, 1);
            double viewLeft = -scrollX / zoom, viewTop = -scrollY / zoom;
            double viewRight = (contentWidth() - scrollX) / zoom, viewBottom = (contentHeight() - scrollY) / zoom;
            if (background != null) {
                for (int x = (int) Math.floor(viewLeft / 16) * 16; x < viewRight; x += 16) {
                    for (int y = (int) Math.floor(viewTop / 16) * 16; y < viewBottom; y += 16) {
                        graphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);
                    }
                }
            }
            for (AdvancementNode node : nodes.values()) {
                if (!visible(node.advancement())) continue;
                Advancement ancestor = node.advancement();
                while (ancestor.parent() != null) {
                    ancestor = ClientAdvancements.DEFINITIONS.get(ancestor.parent().toString());
                    if (ancestor == null) break;
                    AdvancementNode parentNode = nodes.get(ancestor.id().toString());
                    if (parentNode == null) continue;
                    int px = nodeX(parentNode) + 13, py = nodeY(parentNode) + 13;
                    int cx = nodeX(node) + 13, cy = nodeY(node) + 13;
                    graphics.fill(Math.min(px, cx), py, Math.max(px, cx) + 1, py + 2, 0xFF888888);
                    graphics.fill(cx, Math.min(py, cy), cx + 2, Math.max(py, cy) + 1, 0xFF888888);
                    break;
                }
            }
            for (AdvancementNode node : nodes.values()) {
                int x = nodeX(node), y = nodeY(node);
                if (x + 26 <= viewLeft || x >= viewRight || y + 26 <= viewTop || y >= viewBottom) continue;
                Advancement advancement = node.advancement();
                if (!visible(advancement)) continue;
                boolean done = ClientAdvancements.progress(advancement).isDone(advancement);
                String frame = AdvancementJson.string(advancement.display(), "frame", "task");
                int border = done ? 0xFFFFCC44 : frame.equals("challenge") ? 0xFFBB77DD : 0xFF777777;
                graphics.fill(x, y, x + 26, y + 26, border);
                graphics.fill(x + 2, y + 2, x + 24, y + 24, done ? 0xFF655523 : 0xFF282828);
                graphics.renderItem(icon(advancement), x + 5, y + 5);
            }
        } finally {
            graphics.popPose();
            GL11.glPopAttrib();
        }
    }

    private ItemStack icon(Advancement advancement) {
        return icons.computeIfAbsent(advancement.id().toString(), id -> AdvancementIcons.icon(advancement.display().getAsJsonObject("icon")));
    }

    private int nodeX(AdvancementNode node) {
        return node.column() * 78;
    }

    private int nodeY(AdvancementNode node) {
        return (int) (node.row() * 52);
    }

    private boolean visible(Advancement advancement) {
        for (Advancement current = advancement; current != null;) {
            if (current.display() != null && AdvancementJson.flag(current.display(), "hidden", false) && !completedBranches.contains(current.id().toString())) return false;
            current = current.parent() == null ? null : ClientAdvancements.DEFINITIONS.get(current.parent().toString());
        }
        return true;
    }

    private void tooltip(Advancement advancement, int mouseX, int mouseY) {
        List<String> lines = new ArrayList<>();
        int width = Math.min(220, graphics.guiWidth() - 24);
        lines.addAll(graphics.split(TextComponentSerializer.fromJson(advancement.display().get("title")), width));
        lines.addAll(graphics.split(TextComponentSerializer.fromJson(advancement.display().get("description")), width));
        AdvancementProgress progress = ClientAdvancements.progress(advancement);
        if (advancement.requirements().groups().size() > 1) lines.add(progress.completedGroups(advancement) + " / " + advancement.requirements().groups().size());
        tooltip(lines, mouseX, mouseY);
    }

    private void tooltip(List<String> lines, int mouseX, int mouseY) {
        int tooltipWidth = lines.stream().mapToInt(graphics.getFont()::getStringWidth).max().orElse(0);
        int x = Math.max(4, Math.min(mouseX + 12, graphics.guiWidth() - tooltipWidth - 12));
        int y = Math.max(4, Math.min(mouseY + 12, graphics.guiHeight() - lines.size() * 11 - 12));
        graphics.fill(x - 4, y - 4, x + tooltipWidth + 4, y + lines.size() * 11 + 2, 0xF0100010);
        for (int line = 0; line < lines.size(); line++) graphics.drawString(lines.get(line), x, y + line * 11, line == 0 ? 0xFFFFFF88 : 0xFFFFFFFF, true);
    }
}
