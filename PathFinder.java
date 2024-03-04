// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 064
// abal 7627
// vela 1859
// guhe 8938

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.stage.WindowEvent;

public class PathFinder extends Application {

    // Interface Items
    private Stage stage;
    private final BorderPane pane = new BorderPane();
    private Pane center;
    private final Button findPathBtn = new Button("Find Path");
    private final Button showConnectionBtn = new Button("Show Connection");
    private final Button newPlaceBtn = new Button("New Place");
    private final Button newConnectionBtn = new Button("New Connection");
    private final Button changeConnectionBtn = new Button("Change Connection");
    private final Image image = new Image("file:europa.gif");
    private ImageView imageView = new ImageView(image);


    private ListGraph<City> cityListGraph = new ListGraph<>();
    private final HashSet<Line> paths = new HashSet<>();

    //is state isCurrentStateSaved
    private boolean isCurrentStateSaved = true;

    // Source and destination city
    private City srcCity;
    private City destCity;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        center = new Pane();
        this.stage = stage;
        pane.setCenter(center);
        center.setId("outputArea");

        VBox vbox = new VBox();
        pane.setTop(vbox);

        // Add menu bar
        MenuBar menu = new MenuBar();
        vbox.getChildren().add(menu);
        menu.setId("menu");
        Menu fileMenu = new Menu("File");
        menu.getMenus().add(fileMenu);
        fileMenu.setId("menuFile");
        MenuItem newMapItem = new MenuItem("New Map");
        fileMenu.getItems().add(newMapItem);
        newMapItem.setId("menuNewMap");
        newMapItem.setOnAction(new NewMapHandler());
        MenuItem openItem = new MenuItem("Open");
        fileMenu.getItems().add(openItem);
        openItem.setId("menuOpenFile");
        openItem.setOnAction(new OpenHandler());
        MenuItem saveItem = new MenuItem("Save");
        fileMenu.getItems().add(saveItem);
        saveItem.setId("menuSaveFile");
        saveItem.setOnAction(new SaveHandler());
        MenuItem saveImageItem = new MenuItem("Save Image");
        fileMenu.getItems().add(saveImageItem);
        saveImageItem.setId("menuSaveImage");
        saveImageItem.setOnAction(new SaveImageHandler());
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);
        exitItem.setId("menuExit");
        exitItem.setOnAction(new ExitItemHandler());

        // Add buttons to panel
        FlowPane btnPane = new FlowPane();
        btnPane.setAlignment(Pos.CENTER);
        btnPane.getChildren().add(findPathBtn);
        findPathBtn.setId("btnFindPath");
        findPathBtn.setOnAction(new FindPathHandler());
        btnPane.getChildren().add(showConnectionBtn);
        showConnectionBtn.setId("btnShowConnection");
        showConnectionBtn.setOnAction(new ShowConnectionHandler());
        btnPane.getChildren().add(newPlaceBtn);
        newPlaceBtn.setId("btnNewPlace");
        newPlaceBtn.setOnAction(new NewPlaceHandler());
        btnPane.getChildren().add(newConnectionBtn);
        newConnectionBtn.setId("btnNewConnection");
        newConnectionBtn.setOnAction(new NewConnectionHandler());
        btnPane.getChildren().add(changeConnectionBtn);
        changeConnectionBtn.setId("btnChangeConnection");
        changeConnectionBtn.setOnAction(new ChangeConnectionHandler());
        vbox.getChildren().add(btnPane);

        Scene scene = new Scene(pane);
        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.setOnCloseRequest(new ExitHandler());
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    // Mouse action event handlers && Color event handler

    class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle (MouseEvent mouseEvent) {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            center.setOnMouseClicked(null);
            newPlaceBtn.setDisable(false);
            center.setCursor(Cursor.DEFAULT);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Name");
            dialog.setContentText("Name of place: ");
            Optional<String> answer = dialog.showAndWait();
            if (answer.isPresent()) {
                City city = new City(answer.get(), x, y);
                city.setId(city.getName());
                center.getChildren().add(city);
                cityListGraph.add(city);
                city.setId(city.getName());
                city.setOnMouseClicked(new ColorHandler());
            }
        }
    }

    class ColorHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            City city = (City)mouseEvent.getSource();
            int counter = 0;
            for (City locations : cityListGraph.getNodes()){
                if (locations.getFill() == Color.RED)
                    counter++;
            }
            if (city.getFill() == Color.BLUE  &&  counter < 2) {
                city.setFill(Color.RED);
                if (srcCity != null){
                    destCity = city;
                }else{
                    srcCity = city;
                }
            } else {
                city.setFill(Color.BLUE);
                if(city == srcCity){
                    srcCity = null;
                }if (city == destCity){
                    destCity = null;
                }
            }
        }
    }

    // File menu-bar buttons, Menu selection

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {

            isCurrentStateSaved = false;

            srcCity = null;
            destCity = null;
            cityListGraph = new ListGraph<>();
            Image europe = new Image("file:europa.gif");
            center.getChildren().clear();
            imageView = new ImageView(europe);
            center.getChildren().add(imageView);
            stage.sizeToScene();
            cityListGraph.getNodes().clear();
        }
    }


    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (isSaved()) {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Unsaved changes, exit anyway?");
                Optional<ButtonType> answer = a.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.CANCEL) {
                    return;
                }
            }
            try {
                srcCity = null;
                destCity = null;
                isCurrentStateSaved = true;
                cityListGraph = new ListGraph<>();
                FileReader fileReader = new FileReader("europa.graph");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();
                Image europe = new Image(line);
                imageView.setImage(europe);
                center.getChildren().clear();
                center.getChildren().add(imageView);
                stage.sizeToScene();
                line = bufferedReader.readLine();
                if (line != null) {
                    String[] split = line.split(";");
                    int counter = 0;
                    while (counter < split.length) {
                        City city = new City(split[counter++], Double.parseDouble(split[counter++]), Double.parseDouble(split[counter++]));
                        city.save();
                        cityListGraph.add(city);
                        city.setId(city.getName());
                        center.getChildren().add(city);
                        city.setOnMouseClicked(new ColorHandler());
                    }
                    line = bufferedReader.readLine();
                    while (line != null) {
                        split = line.split(";");
                        City srcCity = null;
                        City destCity = null;
                        for (City city : cityListGraph.getNodes()) {
                            if (city.getName().equals(split[0])) {
                                srcCity = city;
                                break;
                            }
                        }
                        for (City city : cityListGraph.getNodes()) {
                            if (city.getName().equals(split[1])) {
                                destCity = city;
                                break;
                            }
                        }
                         if (cityListGraph.getEdgeBetween(srcCity, destCity) == null) {
                            cityListGraph.connect(srcCity, destCity, split[2], Integer.parseInt(split[3]));
                            assert srcCity != null;
                            assert destCity != null;
                            Line mapLine = new Line(srcCity.getCenterX(), srcCity.getCenterY(), destCity.getCenterX(), destCity.getCenterY());
                            mapLine.setStroke(Color.BLACK);
                            paths.add(mapLine);
                            center.getChildren().add(mapLine);
                            mapLine.setDisable(true);
                        }
                        line = bufferedReader.readLine();
                    }

                    fileReader.close();
                    bufferedReader.close();
                }
            } catch (FileNotFoundException e) {
                System.err.print("FILE ERROR: " + e.getMessage());
            } catch (IOException e) {
                System.err.print("IO ERROR: " + e.getMessage());
            }

        }
    }


    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            saveFile();
            isCurrentStateSaved = true;
        }
    }



    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                WritableImage image = center.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR, "IO-Error: " + e.getMessage());
                a.showAndWait();
            }
        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle (WindowEvent windowEvent) {
            if (isSaved()) {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Unsaved changes, exit anyway?");
                Optional<ButtonType> answer = a.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.CANCEL) {
                    windowEvent.consume();
                }
            }
        }
    }

    private boolean isSaved() {
        for (City city : cityListGraph.getNodes()) {
            if (!city.isSaved())
                return true;
        }
        return !isCurrentStateSaved;
    }

    private void saveFile() {
        try {
            FileWriter fileWriter = new FileWriter("europa.graph");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("file:europa.gif");
            int counter = 0;
            for (City city : cityListGraph.getNodes()) {
                printWriter.print(city.getName() + ";" + city.getCenterX() + ";" + city.getCenterY());
                counter++;
                city.save();
                if (counter < cityListGraph.getNodes().size()) {
                    printWriter.print(";");
                }
            }
            for (City city : cityListGraph.getNodes()) {
                for (Edge<City> e : cityListGraph.getEdgesFrom(city)) {
                    printWriter.print("\n" + city.getName() + ";" + e.getDestination().getName() + ";" + e.getName() + ";" + e.getWeight());
                }
            }
            printWriter.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (IOException e) {
            System.err.println("IO Error " + e.getMessage());
        }
    }


    // Buttons (Knappar)

    class NewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent mouseEvent) {
            center.setOnMouseClicked(new ClickHandler());
            newPlaceBtn.setDisable(true);
            center.setCursor(Cursor.CROSSHAIR);
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (srcCity == null || destCity == null) {
                Alert placeAlert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
                placeAlert.showAndWait();
            } else {
                isCurrentStateSaved = false;
                if (cityListGraph.getEdgeBetween(srcCity, destCity) != null) {
                    Alert connectionAlert = new Alert(Alert.AlertType.ERROR, "Connection already exists!");
                    connectionAlert.showAndWait();
                } else {
                    Dialog dialog = new Dialog();
                    dialog.setHeaderText("Connection from " + srcCity.getName() + " to " + destCity.getName());
                    dialog.showAndWait();
                    String nameAns;
                    int timeAns;
                    nameAns = dialog.getNameField();
                    timeAns = dialog.getTimeField();
                    if (!nameAns.isEmpty()) {
                        cityListGraph.connect(srcCity, destCity, nameAns, timeAns);
                        Line line = new Line(srcCity.getCenterX(), srcCity.getCenterY(), destCity.getCenterX(), destCity.getCenterY());
                        line.setStroke(Color.BLACK);
                        paths.add(line);
                        center.getChildren().add(line);
                        line.setDisable(true);
                    }
                }
            }
        }
    }


    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (srcCity == null || destCity == null) {
                Alert placeAlert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
                placeAlert.showAndWait();
            }
            if (!cityListGraph.pathExists(srcCity, destCity)) {
                Alert illegalStateAlert = new Alert(Alert.AlertType.ERROR, "Connection does not exist");
                illegalStateAlert.showAndWait();

            } else {
                if (srcCity != null && destCity != null) {
                    TextField nameField = new TextField();
                    TextField timeField = new TextField();
                    Dialog dialog = new Dialog(nameField, timeField);
                    dialog.setHeaderText("Connection from " + srcCity.getName() + " to " + destCity.getName());
                    nameField.setEditable(false);
                    timeField.setEditable(false);
                    nameField.setText(cityListGraph.getEdgeBetween(srcCity, destCity).getName());
                    timeField.setText(cityListGraph.getEdgeBetween(srcCity, destCity).getWeight() + "");
                    dialog.showAndWait();
                }
            }
        }
    }


    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (srcCity == null || destCity == null) {
                Alert placeAlert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
                placeAlert.showAndWait();

            } try {
                if (srcCity != null && destCity != null) {
                    int timeAns;
                    TextField nameField = new TextField();
                    TextField timeField = new TextField();
                    Dialog dialog = new Dialog(nameField, timeField);
                    dialog.setHeaderText("Connection from " + srcCity.getName() + " to " + destCity.getName());
                    nameField.setEditable(false);
                    nameField.setText(cityListGraph.getEdgeBetween(srcCity, destCity).getName());
                    dialog.showAndWait();
                    timeAns = dialog.getTimeField();
                    cityListGraph.setConnectionWeight(srcCity, destCity, timeAns);
                }
            }catch (NumberFormatException e){
                Alert incorrectFormatAlert = new Alert(Alert.AlertType.ERROR, "Incorrect datatype");
                incorrectFormatAlert.showAndWait();
            }
        }
    }


    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            if (srcCity == null || destCity == null) {
                Alert placeAlert = new Alert(Alert.AlertType.ERROR, "Two places must be selected!");
                placeAlert.showAndWait();
            } try {
                List<Edge<City>> pathList = cityListGraph.getPath(srcCity, destCity);
                if (pathList == null || pathList.isEmpty()) {
                    Alert noPathsAlert = new Alert(Alert.AlertType.ERROR, "No paths exist!");
                    noPathsAlert.showAndWait();
                } else {
                    int totalTime = 0;
                    TextArea textArea = new TextArea();
                    Dialog dialog = new Dialog(textArea);
                    dialog.setHeaderText("The paths from " + srcCity.getName() + " to " + destCity.getName());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Edge<City> e : pathList) {
                        totalTime += e.getWeight();
                        stringBuilder.append("to ").append(e.getDestination().getName()).append(" by ").append(e.getName()).append(" takes ").append(e.getWeight()).append("\n");
                    }
                    stringBuilder.append("Total ").append(totalTime);
                    textArea.setText(stringBuilder.toString());
                    dialog.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}