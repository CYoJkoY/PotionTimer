package com.potiontimer.potiontimermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

@Mod(modid = PotionTimer.MODID, version = PotionTimer.VERSION)
public class PotionTimer {
    public static final String MODID = "potiontimermod";
    public static final String VERSION = "1.1"; // 升级版本号

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PotionHUDHandler());
    }

    @SideOnly(Side.CLIENT)
    public static class PotionHUDHandler {

        @SubscribeEvent
        public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player == null) return;

            Collection<PotionEffect> activeEffects = mc.player.getActivePotionEffects();
            if (activeEffects.isEmpty()) return;

            FontRenderer fontRenderer = mc.fontRenderer;
            // 药水信息显示位置（左上角）
            int posX = 5; // 左对齐
            int posY = 5; // 顶部开始

            for (PotionEffect effect : activeEffects) {
                if (effect == null) continue;
                
                // 跳过剩余时间超过10分钟的效果
                int remainingTicks = effect.getDuration();
                if (remainingTicks > 10 * 60 * 20) { // 10分钟 * 60秒 * 20tick/秒
                    continue;
                }
                
                // 获取药水名称和剩余时间
                Potion potion = effect.getPotion();
                String potionName = I18n.format(potion.getName());
                String durationText = formatDuration(remainingTicks);

                // 组合显示文本
                String displayText = potionName + ": " + durationText;
                
                // 绘制带阴影的文本（左对齐）
                fontRenderer.drawStringWithShadow(displayText, posX, posY, getPotionColor(potion));
                
                // 移动到下一行
                posY += fontRenderer.FONT_HEIGHT + 2;
            }
        }

        // 根据药水类型获取不同颜色
        private int getPotionColor(Potion potion) {
            if (potion.isBadEffect()) {
                return 0xFF5555; // 红色 - 负面效果
            } else {
                return 0x55FF55; // 绿色 - 正面效果
            }
        }

        private String formatDuration(int ticks) {
            int totalSeconds = ticks / 20; // 将tick转换为秒
            
            // 计算分秒
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            
            if (minutes > 0) {
                return String.format("%d:%02d", minutes, seconds);
            } else {
                return String.format("%ds", seconds); // 10分钟以内只显示秒
            }
        }
    }
}