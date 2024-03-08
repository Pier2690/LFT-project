import java.io.*;
import java.util.Scanner;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0; // numero variabili

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.err.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error: match()");
    }

    // Guida = {while, cond, {, assign, print, read}
    public void prog() {

        switch (look.tag) {
            case Tag.WHILE:
            case Tag.COND:
            case '{':
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
                int lnext_prog = code.newLabel();
                statlist(lnext_prog);
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (java.io.IOException e) {
                    System.out.println("IO error\n");
                }
                break;
            default:
                error("syntax error: prog()");
        }
    }

    // Guida = {while, cond, {, assign, print, read}
    public void statlist(int lnext_statlist) {
        switch (look.tag) {
            case Tag.WHILE:
            case Tag.COND:
            case '{':
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
                stat(lnext_statlist);
                statlistp(lnext_statlist);
                break;
            default:
                error("syntax error: statlist()");
        }
    }

    // Guida = {';', EOF, '}'}

    public void statlistp(int lnext_statlistp) {
        switch (look.tag) {
            case ';':
                int lnext2 = code.newLabel();
                code.emit(OpCode.GOto, lnext2);
                code.emitLabel(lnext2);
                match(';');
                stat(lnext_statlistp);
                statlistp(lnext_statlistp);
                break;
            case Tag.EOF:
                lnext2 = code.newLabel();
                code.emit(OpCode.GOto, lnext2);
                code.emitLabel(lnext2);
                break;
            case '}':
                break;
            default:
                error("syntax error: statlistp()");
                break;
        }
    }

    // Guida = {assign, while, cond, print, read, '{'}
    public void stat(int Snext) {

        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(Tag.ASSIGN);
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                int true_W = code.newLabel();
                int false_W = code.newLabel();
                bexpr(true_W);
                code.emit(OpCode.GOto, false_W);
                code.emitLabel(true_W);
                match(')');
                stat(Snext);
                code.emit(OpCode.GOto, true_W - 1);
                code.emitLabel(false_W);
                break;

            case Tag.COND:
                int false_C = code.newLabel();
                match(Tag.COND);
                match('[');
                optlist(false_C);
                match(']');
                conditional(false_C);
                code.emitLabel(false_C);
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('[');
                exprlist(Tag.PRINT);
                match(']');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('[');
                idlist(Tag.READ);
                match(']');
                break;

            case '{':
                match('{');
                statlist(Snext);
                match('}');
                break;
            default:
                error("syntax error: stat()");
                break;
        }
    }

    // GUIDA = {else, end}
    public void conditional(int Snext) {
        switch (look.tag) {
            case Tag.ELSE:
                match(Tag.ELSE);
                stat(Snext);
                match(Tag.END);
                code.emit(OpCode.GOto, Snext);
                break;
            case Tag.END:
                match(Tag.END);
                code.emit(OpCode.GOto, Snext);
                break;
            default:
                error("syntax error: conditional()");
        }
    }

    // GUIDA = {id}
    private void idlist(int Snext) {
        switch (look.tag) {
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                idlistp(Snext, id_addr);
                break;
            default:
                error("syntax error: idlist()");
        }
    }

    // Guida = {',', ']','}',';', end, eof}
    public void idlistp(int Snext, int indirizzo) {
        switch (look.tag) {
            case ',':
                if (Snext == Tag.ASSIGN)
                    code.emit(OpCode.dup);
                match(',');
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                if (Snext == Tag.READ)
                    code.emit(OpCode.invokestatic, 0);
                if (Snext == Tag.ASSIGN || Snext == Tag.READ)
                    code.emit(OpCode.istore, indirizzo);
                idlistp(Snext, id_addr);
                break;
            case ']':
            case Tag.END:
            case Tag.EOF:
            case ';':
            case '}':
                if (Snext == Tag.READ)
                    code.emit(OpCode.invokestatic, 0);
                if (Snext == Tag.ASSIGN || Snext == Tag.READ)
                    code.emit(OpCode.istore, indirizzo);

                break;
            default:
                error("Syntax error: idlistp(): ");
                break;
        }

    }

    // Guida = {option}
    public void optitem(int Snext) {
        if (look.tag == Tag.OPTION) {
            match(Tag.OPTION);
            match('(');
            int true_opt = code.newLabel();
            int false_opt = code.newLabel();
            bexpr(true_opt);
            code.emit(OpCode.GOto, false_opt);
            code.emitLabel(true_opt);
            match(')');
            match(Tag.DO);
            stat(Snext);
            code.emit(OpCode.GOto, Snext);
            code.emitLabel(false_opt);
        } else {
            error("syntax error: errore: optitem()");
        }
    }

    // Guida = {option}
    public void optlist(int Snext) {
        if (look.tag == Tag.OPTION) {
            optitem(Snext);
            optlistp(Snext);
        } else {
            error("syntax error: errore in optlist()");
        }
    }

    // Guida = {option, ']'}
    public void optlistp(int Snext) {
        switch (look.tag) {
            case Tag.OPTION:
                optitem(Snext);
                optlistp(Snext);
            case ']':
                break;
            case Tag.EOF:
                match(Tag.EOF);
                break;
            default:
                error("syntax error: optlist()");
                break;
        }

    }

    // Guida = {<,>,==,<=,>=,<>}
    public void bexpr(int true_bexpr) {

        if (look.tag == (Tag.RELOP)) {
            if (look == Word.lt) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmplt, true_bexpr);

            } else if (look == Word.gt) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmpgt, true_bexpr);

            } else if (look == Word.eq) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmpeq, true_bexpr);

            } else if (look == Word.le) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmple, true_bexpr);
            } else if (look == Word.ne) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmpne, true_bexpr);
            } else if (look == Word.ge) {
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmpge, true_bexpr);
            } else {
                error("syntax error: simbolo sbagliato o mancante");
            }
        }
    }

    // Guida = {+ , -, *, /, num, id}
    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist('+');
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                match('(');
                exprlist('*');
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                int read_num = ((NumberTok) look).lexeme;
                code.emit(OpCode.ldc, read_num);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    error("variabile non trovata: expr()");
                }
                code.emit(OpCode.iload, id_addr);
                match(Tag.ID);
                break;
            default:
                error("syntax error: expr()");

        }
    }

    // Guida = {+, -, *, /, ')', NUM, ID}
    public void exprlist(int Snext) {
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case ')':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp(Snext);
                break;
            default:
                error("syntax error: exprlist()");
        }
    }

    // Guida = {',', ')', ']'}
    public void exprlistp(int Snext) {
        switch (look.tag) {
            case ',':
                if (Snext == Tag.PRINT)
                    code.emit(OpCode.invokestatic, 1);
                match(',');
                expr();
                exprlistp(Snext);
                if (Snext == Token.plus.tag)
                    code.emit(OpCode.iadd);
                else if (Snext == Token.mult.tag)
                    code.emit(OpCode.imul);
                break;
            case ')':
            case ']':
                if (Snext == Tag.PRINT)
                    code.emit(OpCode.invokestatic, 1);
                break;
            default:
                error("syntax error: exprlistp()");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "lettura2.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
