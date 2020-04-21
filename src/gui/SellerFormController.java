package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.VendedorService;

public class SellerFormController implements Initializable {

	private Vendedor entidade;
	private VendedorService servico;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private Label labelErrorNome;
	@FXML
	private TextField txtEmail;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private DatePicker dpDataNascimento;
	@FXML
	private Label labelErrorDataNascimento;
	@FXML
	private TextField txtSalarioBase;
	@FXML
	private Label labelErrorSalarioBase;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;
	
	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}
	
	public void setVendedorService(VendedorService servico) {
		this.servico = servico;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
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
			notifyDataChangeListener();
			Utils.currentStage(evento).close();
		}
		catch(DbException e) {
			Alerts.showAlert("Erro salvando objeto.", null, e.getMessage(), AlertType.ERROR);
		}
		catch(ValidationException e) {
			setMsgsErro(e.getErros());
		}
		
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}	
	}

	private Vendedor getFormData() {
		Vendedor dep = new Vendedor();
		
		ValidationException exception = new ValidationException("Erro na valida��o");
		
		dep.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "Campo n�o pode ser vazio.");
		}
		dep.setNome(txtNome.getText());
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}
		
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
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpDataNascimento, "dd/MM/yyyy");
	}
	
	public void updateFormData() {
		if(entidade == null) {
			throw new IllegalStateException("Entidade est� nula.");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));
		if(entidade.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(entidade.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
	}
	
	private void setMsgsErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if(campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}
	}
}
