package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {

	private Connection conn;

	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insere(Departamento obj) {

		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO departamento (Nome)\r\n" + "VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			int linhasAfetadas = st.executeUpdate();

			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1)); // 1 é a posição do Id
				}
			}
			else {
				System.out.println("Erro inesperado! Registro não foi inserido.");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void atualiza(Departamento obj) {

		PreparedStatement st = null;

		try {
			st = conn.prepareStatement(
					"UPDATE departamento\r\n" + 
					"   SET Nome = ?\r\n" + 
					" WHERE Id = ?");
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void excluirPorId(Integer id) {

		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("DELETE FROM departamento WHERE Id = ?");
			st.setInt(1, id);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Departamento buscaPorId(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM departamento WHERE Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Departamento dep = instanciaDepartamento(rs);
				return dep;
			}

			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Departamento> buscaTudo() {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			List<Departamento> dep = new ArrayList<Departamento>();
			st = conn.prepareStatement("SELECT * FROM departamento ORDER BY Nome");
			rs = st.executeQuery();

			while (rs.next()) {
				dep.add(instanciaDepartamento(rs));
			}
			return dep;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		return new Departamento(rs.getInt("Id"), rs.getString("Nome"));
	}

}
