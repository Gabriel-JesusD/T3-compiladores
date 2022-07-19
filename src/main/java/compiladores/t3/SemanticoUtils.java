package compiladores.t3;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import compiladores.AlgumaParser;
import compiladores.AlgumaParser.FatorContext;
import compiladores.AlgumaParser.ParcelaContext;
import compiladores.AlgumaParser.TermoContext;

public class SemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        int coluna = t.getCharPositionInLine();
        errosSemanticos.add(String.format("Erro %d:%d - %s", linha, coluna, mensagem));
    }
    
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Exp_aritmeticaContext ctx) {
        Table.Tipos ret = null;
        for (TermoContext ta : ctx.termo()) {
            Table.Tipos aux = verificarTipo(tabela, ta);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                adicionarErroSemantico(ctx.start, "Expressão " + ctx.getText() + " contém tipos incompatíveis");
                ret = Table.Tipos.INVALIDO;
            }
        }

        return ret;
    }

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.TermoContext ctx) {
        Table.Tipos ret = null;

        for (FatorContext fa : ctx.fator()) {
            Table.Tipos aux = verificarTipo(tabela, fa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
                ret = Table.Tipos.INVALIDO;
            }
        }
        return ret;
    }
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.FatorContext ctx) {
        Table.Tipos ret = null;

        for (ParcelaContext fa : ctx.parcela()) {
            Table.Tipos aux = verificarTipo(tabela, fa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                adicionarErroSemantico(ctx.start, "Termo " + ctx.getText() + " contém tipos incompatíveis");
                ret = Table.Tipos.INVALIDO;
            }
        }
        return ret;
    }
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.ParcelaContext ctx) {
        Table.Tipos ret = null;

        if(ctx.parcela_nao_unario() != null){
            return verificarTipo(tabela, ctx.parcela_nao_unario());
        }
        // if(ctx.parcela_nao_unario() != null){
            return verificarTipo(tabela, ctx.parcela_unario());
        // }
    }

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) {
            return Table.Tipos.CADEIA;
        }
        if (ctx.identificador() != null) {
            return verificarTipo(tabela, ctx.identificador());
        }
        // se não for nenhum dos tipos acima, só pode ser uma expressão
        // entre parêntesis
        return verificarTipo(tabela, ctx.expressao());
    }

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.IdentificadorContext ctx) {//kk suspeitos
        if (ctx.IDENT() != null) {
            String nomeVar;
            for(int i = 0; i < ctx.IDENT().size(); i++){
                nomeVar += ctx.IDENT().get(i);
            }
            if (!tabela.exists(nomeVar)) {
                adicionarErroSemantico(ctx.IDENT().get(0).getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
                return Table.Tipos.INVALIDO;
            }
            // return verificarTipo(tabela, nomeVar);
        }
        // se não for nenhum dos tipos acima, só pode ser uma expressão
        // entre parêntesis
        return verificarTipo(tabela, ctx.dimensao());
    }
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.DimensaoContext ctx) {//kk suspeitos
        // se não for nenhum dos tipos acima, só pode ser uma expressão
        // entre parêntesis
        return verificarTipo(tabela, ctx.exp_aritmetica(0));
    }

    

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            return Table.Tipos.INT;
        }
        if (ctx.NUM_REAL() != null) {
            return Table.Tipos.REAL;
        }
        if (ctx.IDENT() != null) {
            String nomeVar = ctx.IDENT().getText();
            if (!tabela.exists(nomeVar)) {
                adicionarErroSemantico(ctx.IDENT().getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
                return Table.Tipos.INVALIDO;
            }
            return verificarTipo(tabela, nomeVar);
        }
        // se não for nenhum dos tipos acima, só pode ser uma expressão
        // entre parêntesis
        return verificarTipo(tabela, ctx.expressao());
    }
    
    public static Table.Tipos verificarTipo(Table tabela, String nomeVar) {
        return tabela.verify(nomeVar);
    }
}
