import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    public static String nmb;

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '[':
                peek = ' ';
                return Token.lpq;

            case ']':
                peek = ' ';
                return Token.rpq;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                readch(br);
                if (peek == '/') {
                    while (peek != (char) -1 && peek != '\r') {
                        readch(br);
                    }
                    return lexical_scan(br);
                } else if (peek == '*') {
                    while (peek != (char) -1) {
                        readch(br);
                        if (peek == '*') {
                            readch(br);
                            if (peek == '/') {
                                readch(br);
                                return lexical_scan(br);
                            }
                        }
                    }
                    return null;

                } else {
                    peek = ' ';
                    return Token.div;
                }

            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;

            // ... gestire i casi di ( ) [ ] { } + - * / ; , ... //

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : " + peek);
                    return null;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : " + peek);
                    return null;
                }

                // ... gestire i casi di || < > <= >= == <> ... //

            case (char) -1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek == '_') {

                    // ... gestire il caso degli identificatori e delle parole chiave //
                    String s = new String();
                    boolean undscore = false;
                    while (Character.isLetterOrDigit(peek) || (peek == '_')) {
                        if (peek != '_') { // ..controllo se la stringa è formata da soli underscore
                            undscore = true;
                        }
                        s = s + peek;
                        readch(br);
                    }
                    // ..casi del word
                    if (s.compareTo("print") == 0) {
                        return Word.print;

                    } else if (s.compareTo("read") == 0) {
                        return Word.read;

                    } else if (s.compareTo("while") == 0) {
                        return Word.whiletok;

                    } else if (s.compareTo("else") == 0) {
                        return Word.elsetok;

                    } else if (s.compareTo("do") == 0) {
                        return Word.dotok;

                    } else if (s.compareTo("read") == 0) {
                        return Word.read;

                    } else if (s.compareTo("end") == 0) {
                        return Word.end;

                    } else if (s.compareTo("else") == 0) {
                        return Word.elsetok;

                    } else if (s.compareTo("conditional") == 0) {
                        return Word.conditional;

                    } else if (s.compareTo("to") == 0) {
                        return Word.to;

                    } else if (s.compareTo("assign") == 0) {
                        return Word.assign;
                    } else if (undscore == false) {
                        return null;
                    } else {
                        return new Word(Tag.ID, s);
                    }

                } else if (Character.isDigit(peek)) {

                    // ... gestire il caso dei numeri
                    int n = Character.getNumericValue(peek);
                    int r;
                    readch(br);
                    while (Character.isDigit(peek)) {
                        r = Character.getNumericValue(peek);
                        n = n * 10 + r;
                        readch(br);
                    }
                    nmb = String.valueOf(n);
                    return new NumberTok(Tag.NUM, String.valueOf(n));
                } else {
                    System.err.println("Erroneous character: "
                            + peek);
                    return null;
                }
        }
    }

    public String getnmb() {
        return nmb;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lettura.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
