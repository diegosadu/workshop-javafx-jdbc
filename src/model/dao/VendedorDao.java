package model.dao;

import java.util.List;

import model.entities.Departamento;
import model.entities.Vendedor;

public interface VendedorDao {

	void insere(Vendedor obj);
	void atualiza(Vendedor obj);
	void excluirPorId(Integer id);
	Vendedor buscaPorId(Integer id);
	List<Vendedor> buscaTudo();
	List<Vendedor> buscaPorDepartamento(Departamento departamento);
}
