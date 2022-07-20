package compiladores.t3;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import compiladores.t3.AlgumaParser.FatorContext;
import compiladores.t3.AlgumaParser.ParcelaContext;
import compiladores.t3.AlgumaParser.TermoContext;

public class SemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        int coluna = t.getCharPositionInLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Exp_aritmeticaContext ctx) {
        Table.Tipos ret = null;
        for (TermoContext ta : ctx.termo()) {
            Table.Tipos aux = verificarTipo(tabela, ta);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
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
        else {
            return verificarTipo(tabela, ctx.parcela_unario());
        }
    }

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) {
            return Table.Tipos.CADEIA;
        }
        // if (ctx.identificador() != null) {
            return verificarTipo(tabela, ctx.identificador());
        // }
        // se não for nenhum dos tipos acima, só pode ser uma expressão
        // entre parêntesis
    }

    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.IdentificadorContext ctx) {//kk suspeitos
        String nomeVar = "";
        for(int i = 0; i < ctx.IDENT().size(); i++){
            nomeVar.concat(ctx.IDENT(i).getText());
            if(i != ctx.IDENT().size() - 1){
                nomeVar.concat(".");
            }
        }
        if (!tabela.exists(nomeVar)) {
            return Table.Tipos.INVALIDO;
        } else{
            return verificarTipo(tabela, nomeVar);
        }
    }
    
    public static Table.Tipos verificarTipo(Table tabela, AlgumaParser.Parcela_unarioContext ctx) {
        // if (ctx.NUM_INT() != null) {
        //     return Table.Tipos.INT;
        // }
        // if (ctx.NUM_REAL() != null) {
        //     return Table.Tipos.REAL;
        // }
        // if (ctx.IDENT() != null) {
        //     String nomeVar = ctx.IDENT().getText();
        //     if (!tabela.exists(nomeVar)) {
        //         adicionarErroSemantico(ctx.IDENT().getSymbol(), "Variável " + nomeVar + " não foi declarada antes do uso");
        //         return Table.Tipos.INVALIDO;
        //     }
        //     return verificarTipo(tabela, nomeVar);
        // }
        return Table.Tipos.INVALIDO;

    }
    
    public static Table.Tipos verificarTipo(Table tabela, String nomeVar) {
        return tabela.verify(nomeVar);
    }
}
