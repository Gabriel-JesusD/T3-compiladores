package compiladores.t3;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import compiladores.AlgumaLexer;
import compiladores.AlgumaParser;

/**
 * Gabriel de Jesus Dantas 773412
 * BCC
 */
public class App 
{
    public static void main( String[] args )
    {
        try(PrintWriter p = new PrintWriter(new File(args[1]))) {//saida
            CharStream c = CharStreams.fromFileName(args[0]);//entrada
            AlgumaLexer lex = new AlgumaLexer(c);
            CommonTokenStream cs = new CommonTokenStream(lex); //conversão para token stream
            AlgumaParser parser = new AlgumaParser(cs);
            parser.removeErrorListeners(); // retirar as mensagens nativas do antlr
            MensagemErro me = new MensagemErro(p); // passar o arquivo de saida para imprimir a mensagem de erro
            parser.addErrorListener(me);
            parser.programa();// realiza parse do programa
               
            p.println("Fim da compilacao");//ao final da compilacao imprime esse sinal

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
