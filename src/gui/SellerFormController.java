package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class SellerFormController implements Initializable {

	private Vendedor entidade;
	private VendedorService servico;
	private DepartamentoService departamentoService;
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
	private ComboBox<Departamento> comboBoxDepartamento;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsList;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}

	public void setServices(VendedorService servico, DepartamentoService departamentoService) {
		this.servico = servico;
		this.departamentoService = departamentoService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula.");
		}
		if (servico == null) {
			throw new IllegalStateException("Serviço está nulo.");
		}

		try {
			entidade = getFormData();
			servico.insereOuAtualiza(entidade);
			notifyDataChangeListener();
			Utils.currentStage(evento).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando objeto.", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setMsgsErro(e.getErros());
		}

	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Vendedor getFormData() {
		Vendedor vend = new Vendedor();

		ValidationException exception = new ValidationException("Erro na validação");

		vend.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErros("nome", "Campo não pode ser vazio.");
		}
		vend.setNome(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErros("email", "Campo não pode ser vazio.");
		}
		vend.setEmail(txtEmail.getText());

		if (dpDataNascimento.getValue() == null) {
			exception.addErros("dataNascimento", "Campo não pode ser vazio.");
		}
		else {
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
		    vend.setDataNascimento(Date.from(instant));
		}
		
		if (txtSalarioBase.getText() == null || txtSalarioBase.getText().trim().equals("")) {
			exception.addErros("salarioBase", "Campo não pode ser vazio.");
		}
		vend.setSalarioBase(Utils.tryParseToDouble(txtSalarioBase.getText()));
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		vend.setDepartamento(comboBoxDepartamento.getValue());
		
		return vend;
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
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula.");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));
		if (entidade.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(entidade.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		if (entidade.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}
		else {
			comboBoxDepartamento.setValue(entidade.getDepartamento());
		}
	}

	public void loadAssociatedObjects() {
		if (departamentoService == null) {
			throw new IllegalStateException("DepartamentoService está nulo.");
		}
		List<Departamento> list = departamentoService.buscaTudo();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
	}

	private void setMsgsErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		/*if (campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}
		else {
			labelErrorNome.setText("");
		}*/
		// operador condicional ternário (mesmo funcionamento do decode. '?' para verdadeiro, ':' para falso )
		labelErrorNome.setText(campos.contains("nome") ? erros.get("nome") : "");
		labelErrorEmail.setText(campos.contains("email") ? erros.get("email") : "");
		labelErrorSalarioBase.setText(campos.contains("salarioBase") ? erros.get("salarioBase") : "");
		labelErrorDataNascimento.setText(campos.contains("dataNascimento") ? erros.get("dataNascimento") : "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}
}
