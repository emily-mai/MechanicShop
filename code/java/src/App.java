import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App extends Application {
    private Stage mainStage;
    private static MechanicShop mechanicShop;
    static {
        try {
            mechanicShop = new MechanicShop("postgres", "5432", "postgres", "password");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        home();
        primaryStage.setTitle("Mechanic Shop");
        primaryStage.show();
    }

    private static GridPane setNewGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private void setSceneTitle(GridPane grid, String s, int i) {
        Text sceneTitle = new Text(s);
        sceneTitle.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 30));
        grid.add(sceneTitle, 0, 0, i, 1);
    }

    private Button backButton() {
        Button backBtn = new Button("BACK");
        backBtn.setOnAction(event -> home());
        return backBtn;
    }

    private void home() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "Welcome!", 2);

        Button btn1 = new Button("ADD CUSTOMER");
        btn1.setOnAction(event -> customerView());
        grid.add(btn1, 0, 1, 1, 1);

        Button btn2 = new Button("ADD MECHANIC");
        btn2.setOnAction(event -> mechanicView());
        grid.add(btn2, 0, 2, 1, 1);

        Button btn3 = new Button("ADD CAR");
        btn3.setOnAction(event -> carView());
        grid.add(btn3, 0, 3, 1, 1);

        Button btn4 = new Button("INSERT SERVICE REQUEST");
        btn4.setOnAction(event -> addServiceRequestView());
        grid.add(btn4, 0, 4, 1, 1);

        Button btn5 = new Button("CLOSE SERVICE REQUEST");
        btn5.setOnAction(event -> closeServiceRequestView());
        grid.add(btn5, 0, 5, 1, 1);

        Button btn6 = new Button("CUSTOMERS WITH BILL < 100");
        btn6.setOnAction(event -> queryView(1, -1, "Customers with bill less than 100"));
        grid.add(btn6, 0, 6, 1, 1);

        Button btn7 = new Button("CUSTOMERS WITH > 20 CARS");
        btn7.setOnAction(event -> queryView(2, -1, "Customers with more than 20 cars"));
        grid.add(btn7, 0, 7, 1, 1);

        Button btn8 = new Button("CARS BEFORE 1995 W/ < 50,000 MILES");
        btn8.setOnAction(event -> queryView(3, -1, "Cars before 1995 with < 50,000 miles"));
        grid.add(btn8, 0, 8, 1, 1);

        TextField k = getTextField(grid, "Insert a value for k:", 9, "([1-9][0-9]*)?");
        Button btn9 = new Button("FIRST K CARS WITH MOST SERVICES");
        btn9.setOnAction(event -> queryView(4, Integer.parseInt(k.getText()), "First k cars with the most services"));
        grid.add(btn9, 0, 10, 1, 1);

        Button btn10 = new Button("TOTAL BILL IN DESCENDING ORDER");
        btn10.setOnAction(event -> queryView(5, -1, "Customers in descending order of their total bill"));
        grid.add(btn10, 0, 11, 1, 1);

        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }


    /**********************************************************************************
     ADD CUSTOMER
     **********************************************************************************/
    private TextField getTextField(GridPane grid, String s0, int i, String s2) {
        Label name = new Label(s0);
        grid.add(name, 0, i, 3, 1);
        TextField nameTextField = new TextField("");
        grid.add(nameTextField, 1, i);
        nameTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches(s2)) ? change : null));
        return nameTextField;
    }

    private void customerView() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "INPUT CUSTOMER INFO", 2);
        //TextField idTextField = getTextField(grid, "ID:", 1, "([0-9]*)?");
        TextField fnameTextField = getTextField(grid, "First Name:", 1, "[A-Za-z ]*");
        TextField lnameTextField = getTextField(grid, "Last Name:", 2, "[A-Za-z ]*");
        TextField phoneTextField = getTextField(grid, "Phone Number:", 3, "([1-9][0-9]*)?");
        TextField addressTextField = getTextField(grid, "Address:", 4, ".*");
        Button btn = new Button("ADD CUSTOMER");
        btn.setOnAction(event -> MechanicShop.AddCustomer(mechanicShop,
                //idTextField.getText(),
                fnameTextField.getText(),
                lnameTextField.getText(),
                phoneTextField.getText(),
                addressTextField.getText()
        ));
        grid.add(btn, 0, 6, 1, 1);
        grid.add(backButton(), 1, 6, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }

    private void mechanicView() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "INPUT MECHANIC INFO", 2);
        //TextField idTextField = getTextField(grid, "ID:", 1, "([0-9]*)?");
        TextField fnameTextField = getTextField(grid, "First Name:", 1, "[A-Za-z ]*");
        TextField lnameTextField = getTextField(grid, "Last Name:", 2, "[A-Za-z ]*");
        TextField experienceTextField = getTextField(grid, "Years of Experience:", 3, "([0-9]*)?");
        Button btn = new Button("ADD MECHANIC");
        btn.setOnAction(event -> MechanicShop.AddMechanic(mechanicShop,
                //idTextField.getText(),
                fnameTextField.getText(),
                lnameTextField.getText(),
                experienceTextField.getText()
        ));
        grid.add(btn, 0, 6, 1, 1);
        grid.add(backButton(), 1, 6, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }

    private void carView() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "INPUT CAR INFO", 2);
        TextField vinTextField = getTextField(grid, "VIN:", 1, "([0-9]*)?");
        TextField makeTextField = getTextField(grid, "Make:", 2, "[A-Za-z ]*");
        TextField modelTextField = getTextField(grid, "Model:", 3, "[A-Za-z ]*");
        TextField yearTextField = getTextField(grid, "Year (> 1970):", 4, "([0-9]*)?");
        Button btn = new Button("ADD CAR");
        btn.setOnAction(event -> MechanicShop.AddCar(mechanicShop,
                vinTextField.getText(),
                makeTextField.getText(),
                modelTextField.getText(),
                yearTextField.getText()
        ));
        grid.add(btn, 0, 6, 1, 1);
        grid.add(backButton(), 1, 6, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }

    private void addServiceRequestView() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "INPUT SERVICE REQUEST INFO", 2);
        TextField ridTextField = getTextField(grid, "Service Request ID:", 1, "([0-9]*)?");
        TextField cidTextField = getTextField(grid, "Customer ID:", 2, "([0-9]*)?");
        TextField vinTextField = getTextField(grid, "VIN:", 3, "([0-9A-Za-z]*)?");

        Label name = new Label("Today's date:");
        DatePicker datePicker = new DatePicker();
        grid.add(name, 0, 4, 3, 1);
        grid.add(datePicker, 1, 4);

        TextField odometerTextField = getTextField(grid, "Odometer Reading:", 5, "([0-9]*)?");
        TextField complainTextField = getTextField(grid, "Complain/Problem:", 6, "[A-Za-z ]*");
        Button btn = new Button("ADD SERVICE REQUEST");
        btn.setOnAction(event -> MechanicShop.InsertServiceRequest(mechanicShop,
                ridTextField.getText(),
                cidTextField.getText(),
                vinTextField.getText(),
                datePicker.getValue(),
                odometerTextField.getText(),
                complainTextField.getText()
        ));
        grid.add(btn, 0, 7, 1, 1);
        grid.add(backButton(), 1, 7, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }

    private void closeServiceRequestView() {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "CLOSE SERVICE REQUEST INFO", 2);
        TextField widTextField = getTextField(grid, "Closed Service Request ID:", 1, "([0-9]*)?");
        TextField ridTextField = getTextField(grid, "Service Request ID:", 2, "([0-9]*)?");
        TextField midTextField = getTextField(grid, "Mechanic ID:", 3, "([0-9]*)?");

        Label name = new Label("Today's date:");
        DatePicker datePicker = new DatePicker();
        grid.add(name, 0, 4, 3, 1);
        grid.add(datePicker, 1, 4);

        TextField commentTextField = getTextField(grid, "Comments:", 5, "[A-Za-z ]*");
        TextField billTextField = getTextField(grid, "Total bill:", 6, "([0-9]*)?");
        Button btn = new Button("ADD CLOSED SERVICE REQUEST");
        btn.setOnAction(event -> MechanicShop.CloseServiceRequest(mechanicShop,
                widTextField.getText(),
                ridTextField.getText(),
                midTextField.getText(),
                commentTextField.getText(),
                billTextField.getText(),
                datePicker.getValue()
        ));
        grid.add(btn, 0, 7, 1, 1);
        grid.add(backButton(), 1, 7, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }

    private void queryView(int query, int k, String queryString) {
        GridPane grid = setNewGrid();
        setSceneTitle(grid, "RESULT", 2);
        Text queryTitle = new Text(queryString);
        queryTitle.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 15));
        grid.add(queryTitle, 0, 1, 2, 1);

        List<List<String>> result = new ArrayList<>();
        try {
            switch (query) {
                case 1 -> result = MechanicShop.ListCustomersWithBillLessThan100(mechanicShop);
                case 2 -> result = MechanicShop.ListCustomersWithMoreThan20Cars(mechanicShop);
                case 3 -> result = MechanicShop.ListCarsBefore1995With50000Milles(mechanicShop);
                case 4 -> result = MechanicShop.ListKCarsWithTheMostServices(mechanicShop, k);
                case 5 -> result = MechanicShop.ListCustomersInDescendingOrderOfTheirTotalBill(mechanicShop);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String[][] resultArray = new String[result.size()][];
        for (int i = 0; i < result.size(); i++) {
            List<String> row = result.get(i);
            resultArray[i] = row.toArray(new String[row.size()]);
        }

        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(resultArray));
        TableView<String[]> table = new TableView<>();
        for (int i = 0; i < resultArray[0].length; i++) {
            TableColumn tc = new TableColumn(resultArray[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            tc.setPrefWidth(90);
            table.getColumns().add(tc);
        }
        table.setItems(data);
        grid.add(table, 1, 2, 2, 1);
        grid.add(backButton(), 1, 3, 2, 1);
        Scene scene = new Scene(grid, 400, 600);
        mainStage.setScene(scene);
    }
}