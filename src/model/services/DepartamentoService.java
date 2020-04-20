package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {

	public List<Departamento> buscaTudo() {
		
		List<Departamento> lista = new ArrayList<Departamento>();
		lista.add(new Departamento(1, "Books"));
		lista.add(new Departamento(2, "Computers"));
		lista.add(new Departamento(3, "Electronics"));
		
		return lista;
	}
}
