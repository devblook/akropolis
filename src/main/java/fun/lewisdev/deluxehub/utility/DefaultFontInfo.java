package fun.lewisdev.deluxehub.utility;

public enum DefaultFontInfo {
    A('A', 5), A_LOWERCASE('a', 5), B('B', 5), B_LOWERCASE('b', 5), C('C', 5), C_LOWERCASE('c', 5), D('D', 5),
    D_LOWERCASE('d', 5), E('E', 5), E_LOWERCASE('e', 5), F('F', 5), F_LOWERCASE('f', 4), G('G', 5), G_LOWERCASE('g', 5),
    H('H', 5), H_LOWERCASE('h', 5), I('I', 3), I_LOWERCASE('i', 1), J('J', 5), J_LOWERCASE('j', 5), K('K', 5),
    K_LOWERCASE('k', 4), L('L', 5), L_LOWERCASE('l', 1), M('M', 5), M_LOWERCASE('m', 5), N('N', 5), N_LOWERCASE('n', 5),
    O('O', 5), O_LOWERCASE('o', 5), P('P', 5), P_LOWERCASE('p', 5), Q('Q', 5), Q_LOWERCASE('q', 5), R('R', 5),
    R_LOWERCASE('r', 5), S('S', 5), S_LOWERCASE('s', 5), T('T', 5), T_LOWERCASE('t', 4), U('U', 5), U_LOWERCASE('u', 5),
    V('V', 5), V_LOWERCASE('v', 5), W('W', 5), W_LOWERCASE('w', 5), X('X', 5), X_LOWERCASE('x', 5), Y('Y', 5),
    Y_LOWERCASE('y', 5), Z('Z', 5), Z_LOWERCASE('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5),
    NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6), NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5),
    ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5), LINE('|', 1),
    TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

    private char character;
    private int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        if (this == DefaultFontInfo.SPACE)
            return this.getLength();

        return this.length + 1;
    }

    public static DefaultFontInfo getDefaultFontInfo(char c) {
        for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
            if (dFI.getCharacter() == c)
                return dFI;
        }

        return DefaultFontInfo.DEFAULT;
    }
}
