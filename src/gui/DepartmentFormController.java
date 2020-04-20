package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class DepartmentFormController implements Initializable {

	private Departamento entidade;
	private DepartamentoService servico;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private Label labelErrorNome;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;
	
	public void setDepartamento(Departamento entidade) {
		this.entidade = entidade;
	}
	
	public void setDepartamentoService(DepartamentoService servico) {
		this.servico = servico;
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if(entidade == null) {
			throw new IllegalStateException("Entidade est� nula.");
		}
		if(servico == null) {
			throw new IllegalStateException("Servi�o est� nulo.");
		}
		
		try {
			entidade = getFormData();
			servico.insereOuAtualiza(entidade);
			Utils.currentStage(evento).close();
		}
		catch(DbException e) {
			Alerts.showAlert("Erro salvando objeto.", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private Departamento getFormData() {
		Departamento dep = new Departamento();
		
		dep.setId(Utils.tryParseToInt(txtId.getText()));
		dep.setNome(txtNome.getText());
		
		return dep;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utils.currentStage(evento).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNodes();
	}

	private void initializaNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	
	public void updateFormData() {
		if(entidade == null) {
			throw new IllegalStateException("Entidade est� nula.");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
}
