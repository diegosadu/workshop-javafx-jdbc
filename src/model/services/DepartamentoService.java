package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoService {

	private DepartamentoDao dao = DaoFactory.criaDepartamentoDao();
	
	public List<Departamento> buscaTudo() {
		return dao.buscaTudo();
	}
}
