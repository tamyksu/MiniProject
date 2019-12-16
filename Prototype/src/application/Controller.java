package application;



import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import client.ChatClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;


public class Controller {

	public static Controller instance;
	
	ChatClient client;
	
	@FXML
    private Button insert;
    
    @FXML
    private Button select;

    @FXML
    private Button update;
    
    private String ip;
    
    public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void initialize(String host) throws IOException, UnknownHostException
    {
		instance = this;
		client = new ChatClient(ip,5555);
		this.initializeTable();
    }
	public static void setInstance(Controller instance) {
		Controller.instance = instance;
	}

	
	public ChatClient getClient() {
		return client;
	}
    public static Controller getInstance() {
		return instance;
	}
    
    @FXML
    private TextArea update_answer;
    @FXML
    private TextArea select_answer;
    @FXML
    private TextArea insert_answer;
    
    
    @FXML
    private TextField initiator_name;

    @FXML
    private TextField system_num;

    @FXML
    private TableView<Person> tableView;
    
    @FXML
    private TableColumn<Person, String> idColumn;

    @FXML
    private TableColumn<Person, String> statusColumn;



	public TableColumn<Person, String> getIdColumn() {
		return idColumn;
	}
	public void setIdColumn(TableColumn<Person, String> idColumn) {
		this.idColumn = idColumn;
	}
	public TableColumn<Person, String> getStatusColumn() {
		return statusColumn;
	}
	public void setStatusColumn(TableColumn<Person, String> statusColumn) {
		this.statusColumn = statusColumn;
	}

	@FXML
    private TextField current_status;

    @FXML
    private TextField status;

    @FXML
    private TextField request_description;

    @FXML
    private TextField handler_name;

    @FXML
    private TextField status1;

    @FXML
    private TextField ID1;

    @FXML
    private TextField ID2;

    @FXML
    private TextField initiator_name1;

    @FXML
    private TextField system_num1;

    @FXML
    private TextField current_status1;

    @FXML
    private TextField status2;

    @FXML
    private TextField request_description1;

    @FXML
    private TextField handler_name1;
	
    @FXML // fx:id="Board"
	private Pane Board; // Value injected by FXMLLoader
    

 
	public void setUpdateAanswer(TextArea answer) {
		this.update_answer = answer;
	}
	public TextArea getUpdateAanswer() {
		return update_answer;
	}
	public void setSelectAanswer(TextArea answer) {
		this.select_answer = answer;
	}
	public TextArea getSelectAanswer() {
		return select_answer;
	}
	public void setInsertAanswer(TextArea answer) {
		this.insert_answer = answer;
	}
	public TextArea getInsertAanswer() {
		return insert_answer;
	}
	public void setClient(ChatClient client) {
		this.client = client;
	}


	public Button getSet() {
		return insert;
	}


	public void setSet(Button set) {
		this.insert = insert;
	}


	public Button getGet() {
		return select;
	}


	public void setGet(Button get) {
		this.select = select;
	}


	public Button getUpate() {
		return update;
	}


	public void setUpate(Button upate) {
		this.update = upate;
	}


	public TextField getInitiator_name() {
		return initiator_name;
	}


	public void setInitiator_name(TextField initiator_name) {
		this.initiator_name = initiator_name;
	}


	public TextField getSystem_num() {
		return system_num;
	}


	public void setSystem_num(TextField system_num) {
		this.system_num = system_num;
	}


	public TextField getCurrent_status() {
		return current_status;
	}


	public void setCurrent_status(TextField current_status) {
		this.current_status = current_status;
	}


	public TextField getStatus() {
		return status;
	}


	public void setStatus(TextField status) {
		this.status = status;
	}


	public TextField getRequest_description() {
		return request_description;
	}


	public void setRequest_description(TextField request_description) {
		this.request_description = request_description;
	}


	public TextField getHandler_name() {
		return handler_name;
	}


	public void setHandler_name(TextField handler_name) {
		this.handler_name = handler_name;
	}


	public TextField getStatus1() {
		return status1;
	}


	public void setStatus1(TextField status1) {
		this.status1 = status1;
	}


	public TextField getID1() {
		return ID1;
	}


	public void setID1(TextField iD1) {
		ID1 = iD1;
	}


	public TextField getID2() {
		return ID2;
	}


	public void setID2(TextField iD2) {
		ID2 = iD2;
	}


	public TextField getInitiator_name1() {
		return initiator_name1;
	}


	public void setInitiator_name1(TextField initiator_name1) {
		this.initiator_name1 = initiator_name1;
	}


	public TextField getSystem_num1() {
		return system_num1;
	}


	public void setSystem_num1(TextField system_num1) {
		this.system_num1 = system_num1;
	}


	public TextField getCurrent_status1() {
		return current_status1;
	}


	public void setCurrent_status1(TextField current_status1) {
		this.current_status1 = current_status1;
	}


	public TextField getStatus2() {
		return status2;
	}


	public void setStatus2(TextField status2) {
		this.status2 = status2;
	}


	public TextField getRequest_description1() {
		return request_description1;
	}


	public void setRequest_description1(TextField request_description1) {
		this.request_description1 = request_description1;
	}


	public TextField getHandler_name1() {
		return handler_name1;
	}


	public void setHandler_name1(TextField handler_name1) {
		this.handler_name1 = handler_name1;
	}


	public Pane getBoard() {
		return Board;
	}


	public void setBoard(Pane board) {
		Board = board;
	}
	public TableView<Person> getTable() {
		return this.tableView;
	}
    public void initializeTable() {
    	client.updateTable();
	}


	/******************************************************resetAction****************************************/
    @FXML
	void buttonSetAction(ActionEvent event) {
		ArrayList<String> data = new ArrayList<String>();
		data.add(initiator_name.getText());	
		data.add(system_num.getText());	
		data.add(current_status.getText());	
		data.add(request_description.getText());	
		data.add(status.getText());	
		data.add(handler_name.getText());	
		
		for(String label: data)
		{
			if(label.isEmpty())
			{
				insert_answer.setText("Info: Fields cannot be empty");
				return;
			}

		}
		
		client.insert(data);
		clearAll();

		}
    @FXML
    void clearAll() {
    	initiator_name.setText("");
    	system_num.setText("");
    	current_status.setText("");
    	request_description.setText("");
    	status.setText("");
    	handler_name.setText("");
    	
    }
    @FXML
    void buttonUpdate(ActionEvent event) {
		ArrayList<String> data = new ArrayList<String>();
		data.add(ID1.getText());
		data.add(status1.getText());
		
		for(String label: data)
		{
			if(label.isEmpty())
			{
				update_answer.setText("Info: Fields cannot be empty");
				return;
			}
		}
		
		client.update(data);
    }
    @FXML
    void buttonSelect(ActionEvent event) {
		ArrayList<String> data = new ArrayList<String>();
		data.add(ID2.getText());
		
		for(String label: data)
		{
			if(label.isEmpty())
			{
				select_answer.setText("Info: Fields cannot be empty");
				return;
			}
		}
		
		client.select(data);
    	
    }
    public static void main(String[] args) 
    {
   
    }
	@SuppressWarnings("unchecked")
	public static void setInTable(ArrayList<String> result) {
		ObservableList<Person> data;
		ArrayList<Person> personList = new ArrayList<Person>();
		if (Controller.instance.getTable().isEditable()==false) {
			Controller.instance.getTable().setEditable(true);
		}
     	for (int i = 0; i < result.size(); i++) {
     		personList.add(new Person(result.get(i++), result.get(i)));
		}
		data = FXCollections.observableArrayList(personList);
		
		Controller.instance.getIdColumn().setCellValueFactory(new PropertyValueFactory<Person, String>("id"));
		Controller.instance.getStatusColumn().setCellValueFactory(new PropertyValueFactory<Person, String>("cs"));

		Controller.instance.getTable().setItems(data);
	}
	
	}
