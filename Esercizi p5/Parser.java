import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        if (look.tag == Tag.WHILE || look.tag == Tag.COND
                || look.tag == '{' || look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ) {
            statlist();
            match(Tag.EOF);
        } else {
            error("syntax error: prog()");
        }
    }

    public void statlist() {
        if (look.tag == Tag.ASSIGN || look.tag == Tag.WHILE || look.tag == Tag.COND
                || look.tag == Tag.READ || look.tag == '{' || look.tag == Tag.PRINT) {
            stat();
            statlistp();
        } else {
            error("syntax error: statlist()");
        }
    }

    public void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case Tag.EOF:
                match(Tag.EOF);
                break;
            case '}':
                break;
            default:
                error("syntax error: statlistp()");
                break;
        }
    }

    public void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;

            case Tag.COND:
                match(Tag.COND);
                match('[');
                optlist();
                match(']');
                conditional();
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('[');
                exprlist();
                match(']');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('[');
                idlist();
                match(']');
                break;

            case '{':
                match('{');
                statlist();
                match('}');
                break;
            default:
                error("syntax error in stat()");
                break;
        }

    }

    public void conditional() {
        if (look.tag == Tag.ELSE) {
            match(Tag.ELSE);
            stat();
            match(Tag.END);
        } else if (look.tag == Tag.END) {
            match(Tag.END);
        } else {
            error("syntax error: conditional");
        }
    }

    public void idlist() {

        if (look.tag == Tag.ID) {
            match(Tag.ID);
            idlistp();
        } else {
            error("syntax error: idlist()");
        }
    }

    public void idlistp() {
        switch (look.tag) {
            case ',':
                match(',');
                match(Tag.ID);
                idlistp();
                break;
            case ']':
            case Tag.END:
            case ';':
            case '}':
                break;
            case Tag.EOF:
                match(Tag.EOF);
                break;
            default:
                error("Syntax error: idlistp()");
                break;
        }

    }

    public void optlist() {
        if (look.tag == Tag.OPTION) {
            optitem();
            optlistp();
        } else {
            error("syntax error: optlist()");
        }
    }

    public void optlistp() {
        switch (look.tag) {
            case Tag.OPTION:
                optitem();
                optlistp();
            case ']':
                break;
            case Tag.EOF:
                match(Tag.EOF);
                break;
            default:
                error("syntax error: optlistp()");
                break;
        }

    }

    public void optitem() {
        if (look.tag == Tag.OPTION) {
            match(Tag.OPTION);
            match('(');
            bexpr();
            match(')');
            match(Tag.DO);
            stat();
        } else {
            error("syntax error: optitem()");
        }
    }

    public void bexpr() {

        if (look.tag == (Tag.RELOP)) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("syntax error: bexpr()");
        }
    }

    public void expr() {

        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Syntax error: expr()");
                break;
        }
    }

    public void exprlist() {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*'
                || look.tag == '/' || look.tag == ')' || look.tag == Tag.NUM || look.tag == Tag.ID) {
            expr();
            exprlistp();
        } else {
            error("syntax error: exprlist()");
        }
    }

    public void exprlistp() {
        switch (look.tag) {
            case ',':
                match(',');
                expr();
                exprlistp();
                break;
            case Tag.EOF:
                match(Tag.EOF);
                break;
            case ')':
            case ']':
                break;
            default:
                error("Syntax error in exprlistp()...");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lettura.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}