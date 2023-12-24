package org.example.Theme;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.font.FontProvider;
import io.vproxy.vfx.manager.font.FontSettings;
import io.vproxy.vfx.theme.impl.DarkTheme;
import io.vproxy.vfx.theme.impl.DarkThemeFontProvider;

/**
 * 该类用于设定程序的主题、字体、图标
 *
 * @author 吴鹄远
 * Date 2023/11/30 19:14
 */
public class ThemeSet extends DarkTheme {
    @Override
    public FontProvider fontProvider() {
        return new IntroFontProvider();
    }

    /**
     * 设定黑暗主题
     *
     * @author 吴鹄远
     * Date 2023/12/24 11:51
     */
    public static class IntroFontProvider extends DarkThemeFontProvider {
        @Override
        protected void defaultFont(FontSettings settings) {
            super.defaultFont(settings);
            settings.setFamily(FontManager.FONT_NAME_JetBrainsMono);
            settings.setSize(15);
        }
    }


}
