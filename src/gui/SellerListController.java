package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Vendedor;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class SellerListController implements Initializable, DataChangeListener {

	private VendedorService servico;

	@FXML
	private TableView<Vendedor> tableViewVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> tableColumnId;
	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;
	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;
	@FXML
	private TableColumn<Vendedor, Date> tableColumnDataNascimento;
	@FXML
	private TableColumn<Vendedor, Double> tableColumnSalarioBase;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEDIT;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnREMOVE;
	@FXML
	private Button btNovo;

	private ObservableList<Vendedor> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.currentStage(evento);
		Vendedor vend = new Vendedor();
		createDialogForm(vend, "/gui/SellerForm.fxml", parentStage);
	}

	public void setVendedorService(VendedorService servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tableColumnDataNascimento, "dd/MM/yyyy");
		tableColumnSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatTableColumnDouble(tableColumnSalarioBase, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Servi�o estava nulo.");
		}
		List<Vendedor> lista = servico.buscaTudo();
		obsList = FXCollections.observableArrayList(lista);
		tableViewVendedor.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Vendedor dep, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setVendedor(dep);
			controller.setServices(new VendedorService(), new DepartamentoService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do vendedor");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Erro carregando tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Vendedor dep) {
		Optional<ButtonType> resultado = Alerts.showConfirmation("Confirmation", "Tem certeza que quer excluir o registro?");
		
		if(resultado.get() == ButtonType.OK) {
			if(servico == null) {
				throw new IllegalStateException("Servi�o est� nulo.");
			}
			
			try{
				servico.exclui(dep);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro excluindo objeto.", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
