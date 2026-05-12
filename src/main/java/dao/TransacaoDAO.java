package dao;
import model.Transacao;

import java.util.List;

public interface TransacaoDAO {
    void salvar(List<Transacao> transacoes);
    List<Transacao> carregar();
}