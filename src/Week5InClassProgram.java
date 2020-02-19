// IMPORTS
// These are some classes that may be useful for completing the project.
// You may have to add others.
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Stack;
/**
 * Web Browser Program
 * Developed by: Brandon Bilson, Audrey Schulte, Shane Hoppe, Nate Leach, Jacob Yankee
 * Version 1?
 */

/**
 * The main class for Week5InClassProgram. Week5InClassProgram constructs the JavaFX window and
 * handles interactions with the dynamic components contained therein.
 */
public class Week5InClassProgram extends Application {
    // INSTANCE VARIABLES
    // These variables are included to get you started.
    private Stage stage = null;
    private Stage newStage = new Stage();
    private BorderPane borderPane = null;
    private WebView view = null;
    private WebEngine webEngine = null;
    private TextField statusbar = null;
    private TextField taskBar = null;
    private HBox taskbar = new HBox();
    // HELPER METHODS
    /**
     * Retrieves the value of a command line argument specified by the index.
     *
     * @param index - position of the argument in the args list.
     * @return The value of the command line argument.
     */
    private String getParameter( int index ) {
        Parameters params = getParameters();
        List<String> parameters = params.getRaw();
        return !parameters.isEmpty() ? parameters.get(index) : "";
    }

    /**
     * Creates a WebView which handles mouse and some keyboard events, and
     * manages scrolling automatically, so there's no need to put it into a ScrollPane.
     * The associated WebEngine is created automatically at construction time.
     *
     * @return browser - a WebView container for the WebEngine.
     */
    private WebView makeHtmlView( ) {
        view = new WebView();
        webEngine = view.getEngine();
        return view;
    }

    /**
     * Generates the status bar layout and text field.
     *
     * @return statusbarPane - the HBox layout that contains the statusbar.
     */

    private HBox makeTaskBar() {
        //taskbar for the bottom of the page to displayed moused over urls/links
        taskbar.setPadding(new Insets(1, 1, 1, 1));
        taskbar.setSpacing(10);
        taskBar = new TextField();
        taskBar.setEditable(false); //not able to be edited
        HBox.setHgrow(taskBar, Priority.ALWAYS);
        taskbar.getChildren().add(taskBar); //added
        return taskbar;
    }


    private HBox makeStatusBar( ) { //contains the box that you type urls into
        HBox statusbarPane = new HBox(); //making the statusbar hbox

        StackPane root = new StackPane();
        Text userInfoText = new Text("Welcome! "+
                "Use the forward and back buttons to navigate! " +
                "The refresh button reloads the page, and you can enter a URL in the address bar, such as https://www.mtu.edu");
        userInfoText.setWrappingWidth(300);

        root.getChildren().addAll(userInfoText);

        Scene helpButtonWindow = new Scene(root,500,500);

        statusbarPane.setPadding(new Insets(4, 3, 4, 3)); //sets padding
        WebHistory history = webEngine.getHistory(); //WebHistory object
        Button refreshButton = new Button("Refresh"); //never needs to be disabled. Always available


        Button helpButton = new Button("?"); //Defining the help button

        Button backButton = new Button("Back"); //back button
        backButton.setOnAction((new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                webEngine.executeScript("history.back()");
            }
        }));
        Button forwardButton = new Button("Forward"); //forward button
        forwardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                webEngine.executeScript("history.forward()");
            }
        });


        helpButton.setOnAction (
                (ActionEvent open ) -> {
                    newStage.setScene(helpButtonWindow);
                    newStage.setTitle("User Help Guide");
                    newStage.show();
                }
        );


        //one of the easiest buttons to implement
        refreshButton.setOnAction(event -> {
            webEngine.reload(); //essentially reloads the current page
        });

        statusbarPane.setSpacing(4); //spacing
        statusbarPane.setStyle("-fx-background-color: #336699;"); //color
        statusbar = new TextField(); //defines a new textfield to enter stuff in, like URLs

        statusbar.setOnKeyPressed((final KeyEvent keyEvent) -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                String addressString = statusbar.getText(); //takes in the test from the statusbar as a string
                if(!addressString.startsWith("http://")){
                    if(!addressString.startsWith("www")){
                        addressString = "www." + addressString;
                    }
                    addressString = "http://" + addressString;
                }
                webEngine.loadContent(addressString); //loads in the url entered.
                webEngine.load(addressString);//loads the url
            }
        });
        HBox.setHgrow(statusbar, Priority.ALWAYS);
        statusbarPane.getChildren().addAll(backButton, forwardButton, refreshButton, statusbar, helpButton); //builds the statusbar
        return statusbarPane;
    }



    public void start(Stage stage) {
        //defining variables for the stage
        BorderPane borderPane = new BorderPane();
        final int FRAME_WIDTH = 860;
        final int FRAME_HEIGHT = 640;

        //Sets the html view and shows the current URL
        borderPane.setCenter(makeHtmlView());
        webEngine.load("https://duckduckgo.com"); //Defaults to DuckDuckGo
        borderPane.setTop(makeStatusBar());
        borderPane.setBottom(makeTaskBar());

        borderPane.setCenter(makeHtmlView());
        String defaultURL = "https://duckduckgo.com";
        String commandLineURL = getParameter(0);
        webEngine.load(commandLineURL.isEmpty() ? defaultURL : commandLineURL);

        //Sets the stage upon opening the browser
        Scene scene1 = new Scene(borderPane,FRAME_WIDTH,FRAME_HEIGHT);
        stage.setScene(scene1);

        //Gets the title of the page
        webEngine.setOnStatusChanged(e->
        {
            taskBar.setText(e.getData());
            if(webEngine.getTitle() != null){ //making sure there is a title present
                stage.setTitle(webEngine.getTitle()); //sets the title of the stage to the webpage
            } else {
                stage.setTitle(view.getEngine().getLocation()); //looks for a title if there isn't one present
            }
        });
        stage.show();
    }


    /**
     * The main( ) method is ignored in JavaFX applications.
     * main( ) serves only as fallback in case the application is launched
     * as a regular Java application, e.g., in IDEs with limited FX
     * support.
     *
     * @param args the command line arguments
     */

    public static void main(String[] args) {

        launch(args);
    }
}