package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.VendedorDao;
import model.entities.Vendedor;

public class VendedorService {

	private VendedorDao dao = DaoFactory.criaVendedorDao();
	
	public List<Vendedor> buscaTudo() {
		return dao.buscaTudo();
	}
	
	public void insereOuAtualiza(Vendedor dep) {
		if(dep.getId() == null) {
			dao.insere(dep);
		}
		else {
			dao.atualiza(dep);
		}
	}
	
	public void exclui(Vendedor dep) {
		dao.excluirPorId(dep.getId());
	}
}
