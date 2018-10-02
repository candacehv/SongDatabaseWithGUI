/**
 * This program creates a database that will read in or create a new
 * song list database. Users can add, edit, and delete songs.
 * It holds the song name, item code, description, artist, album, and price
 * of each song
 * @author Candace Holcombe-Volke
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.stage.*; 
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.collections.*;
import javafx.event.*;

/**
 * The SongDatabase class does most of the work of the program with
 * anonymous inner classes inside the start() method. It
 * holds the main() method of the program, and initiates the GUI 
 *  by overriding the start().
 *  
 *  The main data is stored in TreeMap called songs, 
 *  ObservableList called songList, and
 *  ListView called loadedSongs.
 * @author Candace Holcombe-Volke
 *
 */
public class SongDatabase extends Application 
{        
    SongDetails songDet = new SongDetails();
    
    private Label songNameLab = new Label( "Selected Song" );
    private Label itemCodeLab = new Label( "Item Code");
    private Label descriptionLab = new Label( "Description" );
    private Label artistLab = new Label( "Artist" );
    private Label albumLab = new Label( "Album" );
    private Label priceLab = new Label( "Price $" );
    
    private ComboBox<String> songListComboBox; 
    private TextField itemCodeField;
    private TextField descriptionField;
    private TextField artistField;
    private TextField albumField;
    private TextField priceField;
    
    private Button addButton; 
    private Button editButton;
    private Button deleteButton; 
    private Button acceptButton;
    private Button cancelButton; 
    private Button exitButton; 
    
    // Message labels
    private Label statusLabel = new Label(); // shows current mode
    private Label errorLabel = new Label(); // shows errors, if any
    
    // songs TreeMap has primary responsibility for storing the database
    private static TreeMap<String, SongDetails> songs = 
        new TreeMap<String, SongDetails>();
    
    // songList and loadedSongs are
    //responsible for managing the display of songs loaded in the combobox
    private static ObservableList<String> songList =
        FXCollections.observableArrayList();
    private static ListView<String> loadedSongs = new ListView<String>(); 

    static String filePath; // collected as command line arg
   
    /**
     * The main() collects the command line args calls readDBFile()
     * to read in the data from a file if it already exists, and creates
     * one if it does not.
     * After running the main(), songs, songList, and loadedSongs will be
     * populated, and filePath will be initialized.
     * @param args is the song database file location
     * @throws IOException  
     * @author Candace Holcombe-Volke
     */
    public static void main( String [] args ) throws IOException 
    {
            SongDatabase db = new SongDatabase();
            filePath = args[0];
            db.readDBFile( args[0] );
            launch( args[0] );
    }
        
    /**
     * The start() begins the GUI component. All interactive aspects of the 
     * program take place in this method, including adding, editing, deleting,
     * songs and and saving a song database file.
     * 
     * First, all text fields are initialized, then comboBox is initialized w/ 
     * handler, then all buttons are initialized with their own anonymous inner
     * handler classes. 
     * This method ends by exitButton listener, which ends the program.
     * 
     * @param mainStage is the main Stage of the program which holds all panes
     * @author Candace Holcombe-Volke
     */
    public void start( Stage mainStage )
    {
       
        // Add controls ( buttonNode, labelNode, stausNode,
        // and textNode) to rootNode. Add rootNode to firstScene. Add
        // firstScene to mainStage
        mainStage.setTitle( "Your Song Database" );
        BorderPane rootNode = new BorderPane(); 
        GridPane topNode = new GridPane();
        GridPane.setMargin(topNode, new Insets (10, 10, 10, 10 ));

        FlowPane buttonNode = new FlowPane( Orientation.VERTICAL );
        buttonNode.setPadding(new Insets( 300, 10, 10, 10 ));
        buttonNode.setHgap(10);
        
        FlowPane labelNode = new FlowPane( Orientation.HORIZONTAL ); 
        labelNode.setPadding( new Insets( 90, 10, 10, 10 ));
        labelNode.setVgap(20);
        
        FlowPane statusNode = new FlowPane( Orientation.VERTICAL );
        statusNode.setPadding( new Insets( 10, 10, 10, 10 ));
        statusLabel.setText( "Current Mode: View");
        statusNode.getChildren().addAll( statusLabel, errorLabel );
       
        
        FlowPane textNode = new FlowPane( Orientation.HORIZONTAL ); 
        textNode.setPadding( new Insets( 90, 10, 10, 100 ));
        textNode.setVgap( 10 );
        rootNode.setTop( statusNode );
        rootNode.getChildren().addAll( labelNode,  buttonNode, textNode );
       
        Scene firstScene = new Scene( rootNode, 700, 400 );
        mainStage.setScene( firstScene ); 
        mainStage.show();
        mainStage.setResizable( true );
        
        
        
        // Initialize songList observableList with TreeMap song names
        for( Map.Entry<String, SongDetails> me : songs.entrySet() )
        {
            songList.add( me.getValue().getSongTitle()); 
        }
        
        
        // Initialize songList combo box
        songListComboBox = new ComboBox<String>( songList );
        songListComboBox.setEditable( false );
        songListComboBox.setVisibleRowCount( 4 );
        songListComboBox.setPrefWidth( 350 );
        //Initialize ListView with the songs in the TreeMap
        Set<Map.Entry<String, SongDetails>> set = songs.entrySet(); 
        for( Map.Entry<String, SongDetails> me : set )
        {
            loadedSongs.getItems().setAll( me.getValue().getSongTitle());
        }
        
        // Create songListComboBox listener, anonymous inner class
        SingleSelectionModel<String> selectionMod = 
            songListComboBox.getSelectionModel(); 
        selectionMod.selectedItemProperty().addListener( 
            new ChangeListener<String>()
        { 
            // listener
            public void changed( ObservableValue<? extends String> change, 
                String oldVal, String newVal ) 
            {
                // get TreeMap entry set to find data linked to song selected
                Set<Map.Entry<String, SongDetails>> set = songs.entrySet(); 
                for( Map.Entry<String, SongDetails> me : set)
                {
                    // find matching key
                    if ( me.getValue().getSongTitle() == newVal ) 
                    {
                        // Get details for the selected song and display 
                        //in text field
                        itemCodeField.setText( me.getKey() );
                        descriptionField.setText ( 
                            me.getValue().getDescription()); 
                        artistField.setText ( me.getValue().getArtist() ); 
                        albumField.setText ( me.getValue().getAlbum() ); 
                        // need to reformat this and get as double
                        priceField.setText(  me.getValue().getPriceString());
                    }
                }
            }
        });
        

        // Create itemCode box
        itemCodeField = new TextField(); 
        itemCodeField.setPrefColumnCount( 8 ); 
        // Create itemCode listener
        itemCodeField.textProperty().addListener( 
            new ChangeListener<String>()
        {
            public void changed( ObservableValue<? extends String>
                change, String oldVal, String newVal )
            {
                if( statusLabel.getText().equalsIgnoreCase(
                    "Current Status: Add Mode"))
                {
                    // prevent accidentally adding a song that already exists
                    if ( songs.containsKey(itemCodeField.getText() )
                        && statusLabel.getText().equalsIgnoreCase(
                            "Current Status: Add Mode" ))
                    {
                        // Error message when trying to enter new song 
                        // with existing item code
                        errorLabel.setText( "That song is already in "
                            + "the database. To edit, first click Cancel, "
                            + "then click Edit to modify." );
                        
                        // Disable the accept button to prevent 
                        //duplicating songs
                        acceptButton.setDisable( true );
                    }
                    else
                    {
                        // Remove error label warning and re-enable accept
                        // button when item code no longer a duplicate
                        errorLabel.setText( "" );
                        acceptButton.setDisable( false );
                    }
                }
            }
        });        
        
        
        // Initialize Description field
        descriptionField = new TextField(); 
        descriptionField.setPrefColumnCount( 30 );
        
        
        // Initialize artist field
        artistField = new TextField(); 
        artistField.setPrefColumnCount( 30 );

        
        // Initialize album field
        albumField = new TextField(); 
        albumField.setPrefColumnCount( 30 );

        
        // Initialize price box
        priceField = new TextField(); 
        priceField.setPrefColumnCount( 30 );

        // Add text fields and combo box to text node
        textNode.getChildren().addAll( songListComboBox, itemCodeField,
            descriptionField,  artistField, albumField, priceField );
        
        // Add labels to label node
        labelNode.getChildren().addAll( songNameLab,
            itemCodeLab, descriptionLab, artistLab, albumLab,
            priceLab ); 
            
        
        
        // Begin button management
        
        // Initialize and handle the Add Button
        addButton = new Button( "Add"); // initialize button
        addButton.setPrefWidth( 100 );
        // addButton handler
        addButton.setOnAction( new EventHandler<ActionEvent>()
        {
            public void handle( ActionEvent event ) 
            {
                statusLabel.setText( "Current Status: Add Mode" );
                
                songListComboBox.setEditable( true );
                songListComboBox.valueProperty().set( null );
                itemCodeField.clear();
                descriptionField.clear();
                artistField.clear();
                albumField.clear();
                priceField.clear();

                songListComboBox.setEditable( true );
                itemCodeField.setEditable( true );
                descriptionField.setEditable( true );
                artistField.setEditable( true );
                albumField.setEditable( true );
                priceField.setEditable( true );
                
                songListComboBox.setDisable(false);
                itemCodeField.setDisable(false);
                descriptionField.setDisable(false);
                artistField.setDisable(false);
                albumField.setDisable(false);
                priceField.setDisable(false);
                
                addButton.setDisable(true);
                editButton.setDisable(true); 
                deleteButton.setDisable(true); 
                acceptButton.setDisable(false); 
                cancelButton.setDisable(false);
                exitButton.setDisable(true);
            }
        });

        
        // Initialize and handle the editButton 
        editButton = new Button( "Edit" ); // initialize button
        editButton.setPrefWidth( 100 );
        // editButton handler
        editButton.setOnAction( new EventHandler<ActionEvent>() 
        {
            public void handle( ActionEvent event ) 
            {
                statusLabel.setText( "Current Status: Edit Mode" );
                
                songListComboBox.setEditable( false );
                itemCodeField.setDisable( true );
                descriptionField.setEditable( true );
                artistField.setEditable( true );
                albumField.setEditable( true );
                priceField.setEditable( true );
                
                addButton.setDisable( true );
                editButton.setDisable( true ); 
                deleteButton.setDisable( true ); 
                acceptButton.setDisable( false ); 
                cancelButton.setDisable( false );
                exitButton.setDisable( true );
            }
        });
        
        // Initialize and handle deleteButton 
        deleteButton = new Button( "Delete" ); // initialize button
        deleteButton.setPrefWidth( 100 );
        // Begin deleteButton handler
        deleteButton.setOnAction( new EventHandler<ActionEvent>()
        {
            public void handle( ActionEvent event ) 
            {
                statusLabel.setText( "Current Status: Delete Mode");
                errorLabel.setText( "Are you sure you want to delete"
                    + " the selected song?");
                
                songListComboBox.setDisable( true );
                itemCodeField.setDisable( true );
                descriptionField.setDisable( true );
                artistField.setDisable( true );
                albumField.setDisable( true );
                priceField.setDisable( true );
                
                addButton.setDisable( true );
                editButton.setDisable( true ); 
                deleteButton.setDisable( true ); 
                acceptButton.setDisable( false ); 
                cancelButton.setDisable( false );
                exitButton.setDisable( true );
            }
        });
        
        // Initialize and Handle acceptButton
        acceptButton = new Button( "Accept" ); // create button
        acceptButton.setPrefWidth( 100 );
        // Begin acceptButton handler
        // Handles clicks for add mode, edit mode, and delete mode and returns
        // to view mode. Throws @throws NullPointerException, 
        // and NumberFormatException
        acceptButton.setOnAction( new EventHandler<ActionEvent>() 
        {
            public void handle( ActionEvent event ) throws 
            NullPointerException, NumberFormatException
            {
                try 
                {
                    // acceptButton handler from Add Mode
                    if ( statusLabel.getText().equalsIgnoreCase(
                        "Current Status: Add Mode" ))
                    {
                        // Verify that all fields have text entered
                        if( !itemCodeField.getText().equalsIgnoreCase( "" )
                            && itemCodeField != null 
                            && !descriptionField.getText().equalsIgnoreCase("")
                            && descriptionField != null 
                            && !artistField.getText().equalsIgnoreCase( "" )
                            && artistField != null 
                            && !albumField.getText().equalsIgnoreCase( "" )
                            && albumField != null 
                            && !priceField.getText().equalsIgnoreCase( "" )
                            && priceField != null )
                        {
                            // Create new song with type SongDetails 
                            SongDetails newSong = new SongDetails( 
                                songListComboBox.getValue(), 
                                itemCodeField.getText(),
                                descriptionField.getText(), 
                                artistField.getText(), 
                                albumField.getText(), priceField.getText());
                            
                            // Add new song of type SongDetails to TreeMap
                            songs.put( newSong.getItemCode(), newSong);
    
                            // Update the songList ListView and observable list
                            songList.clear();
                            Set<Map.Entry<String, SongDetails>> set =
                                songs.entrySet(); 
                            for( Map.Entry<String, SongDetails> me : set)
                            {
                                loadedSongs.getItems().setAll(
                                    me.getValue().getSongTitle()); 
                                songList.add( me.getValue().getSongTitle()); 
                            }
                            returnToViewMode();
                        }
                        else // Update errorLabel if any fields are blank
                        {
                            if( songListComboBox.getValue().equalsIgnoreCase(
                                "" ) || songListComboBox == null)
                            {
                                errorLabel.setText( 
                                "You must enter the song title." );
                            }
                            if( itemCodeField.getText().equalsIgnoreCase( "" )
                                || itemCodeField == null)
                            {
                                errorLabel.setText( 
                                "You must enter the item code." );
                            }
                            else if( 
                                descriptionField.getText().equalsIgnoreCase(
                                    "" )
                                || descriptionField == null )
                            {
                                errorLabel.setText( 
                                "You must enter the song description." );
                            }
                            else if( artistField.getText().equalsIgnoreCase(
                                "" ) || artistField == null )
                            {
                                errorLabel.setText( 
                                "You must enter the artist name." );
                            }
                            else if( albumField.getText().equalsIgnoreCase( "")
                                || albumField == null )
                            {
                                errorLabel.setText( 
                                "You must enter the album name.");
                            }
                            else if( priceField.getText().equalsIgnoreCase( "")
                                || priceField == null )
                            {
                                errorLabel.setText(
                                "You must enter the song price." );
                            }
                        } 
                    } // End acceptButton conditions for Add Mode
                
                    
                    // acceptButton handling for Edit Mode
                    else if( statusLabel.getText().equalsIgnoreCase(
                        "Current Status: Edit Mode" ) )
                    {
                        // Create new song with type SongDetails 
                        SongDetails newSong = new SongDetails( 
                            songListComboBox.getValue(), 
                            itemCodeField.getText(), 
                            descriptionField.getText(), 
                            artistField.getText(), 
                            albumField.getText(), priceField.getText());
                        
                        // Add newSong of type SongDetails to TreeMap
                        songs.put( newSong.getItemCode(), newSong );

                        // Update the songList ListView and observable list
                        songList.clear();
                        Set<Map.Entry<String, SongDetails>> set =
                            songs.entrySet(); 
                        for( Map.Entry<String, SongDetails> me : set)
                        {
                            loadedSongs.getItems().setAll(
                                me.getValue().getSongTitle()); 
                            songList.add( me.getValue().getSongTitle()); 
                        }
                        returnToViewMode();
                    }
                    
                    // Begin listener for Delete Mode
                    else if ( statusLabel.getText().equalsIgnoreCase(
                        "Current Status: Delete Mode" ))
                    {
                        // Remove song from songs, and songList (which will
                        // update loadedSongs
                        songs.remove( itemCodeField.getText() ); 
                        int index = 
                            songListComboBox.getSelectionModel().getSelectedIndex();
                        songList.remove( index ); 
                        
                        returnToViewMode();
                    } // End accept hander for Delete Mode
                }
                
                // catch null pointer if song title is empty 
                catch (NullPointerException exception )
                {
                    errorLabel.setText( 
                        "You must enter a song title to continue." );
                }
                // Catch exception of price is non-numeric
                catch( NumberFormatException numExc)
                {
                    errorLabel.setText( "The price must be a number.");
                }
            } // end all acceptButton listening
        }); 
        
        
        
        // Create and handle cancelButton
        cancelButton = new Button( "Cancel" ); // create button
        cancelButton.setPrefWidth( 100 );
        cancelButton.setOnAction( new EventHandler<ActionEvent>()
        {
            // Begin cancelButton handler
           public void handle( ActionEvent event)
           {
               returnToViewMode();
           }
        }); 
        
        // Initialize exitButton and handler
        exitButton = new Button("Exit"); // create button
        exitButton.setPrefWidth( 100 );
        // create event handler 
        exitButton.setOnAction( new EventHandler<ActionEvent>()
        {
            public void handle( ActionEvent event )
            {
                try 
                {
                    writeOut();
                    System.exit(0);  
                } 
                  
                catch (IOException event2) 
                {
                    System.out.println("Error: Something went wrong and "
                        + "Your input is not accepted. Please try again.");
                }
            }
        }); 

        
        // Add all buttons to the buttonNode
        buttonNode.getChildren().addAll( addButton, editButton, deleteButton,
            acceptButton, cancelButton, exitButton );

        
        
        // Initial state of controls 
        if( songList.isEmpty() )
        {
            songListComboBox.setEditable( false );
            songListComboBox.setPromptText( "Database Empty. Add some songs!");
            songListComboBox.getSelectionModel().select( 0 );
            loadedSongs.setDisable( true ); 
    
            songListComboBox.setDisable( false );
            itemCodeField.setEditable( false );
            descriptionField.setEditable( false );
            artistField.setEditable( false );
            albumField.setEditable( false );
            priceField.setEditable( false );
            
            addButton.setDisable( false );
            editButton.setDisable( false ); 
            deleteButton.setDisable( false ); 
            acceptButton.setDisable( true ); 
            cancelButton.setDisable( true );
            exitButton.setDisable( false );
        }
        // Initial state of controls if database is empty
        else if( !songList.isEmpty() )
        {
            returnToViewMode();
        }
    } 
    
    public void returnToViewMode()
    {
     // Enable/Disable appropriate controls for view mode
        songListComboBox.setEditable( false );
        songListComboBox.getSelectionModel().select( 0 );
        loadedSongs.setDisable( true ); 

        songListComboBox.setDisable( false );
        itemCodeField.setEditable( false );
        itemCodeField.setDisable( false );
        descriptionField.setEditable( false );
        descriptionField.setDisable( false );
        artistField.setEditable( false );
        artistField.setDisable( false );
        albumField.setEditable( false );
        albumField.setDisable( false );
        priceField.setEditable( false );
        priceField.setDisable( false );
        
        addButton.setDisable( false );
        editButton.setDisable( false ); 
        deleteButton.setDisable( false ); 
        acceptButton.setDisable( true ); 
        cancelButton.setDisable( true );
        exitButton.setDisable( false );
        
        // Update status and error labels
        statusLabel.setText( "Current Status: View Mode" );
        errorLabel.setText( "" );
    }
    
    /**
     * readDBFile uses command line args to find file if it exists
     * and read in the data using a BufferedReader. It initializes 
     * the songs TreeMap and catches 
     * FileNotFoundException if the file doesn't exist and prompts user
     * to ask if it should create one. 
     * 
     * @param fp is the command line arg which is the file location
     * @throws IOException if erro on input or output of file
     * @throws FileNotFoundException if database doesn't exist
     */
    public void readDBFile( String fp ) throws IOException
    {
        String line;
        // Read in the data
        try( BufferedReader songDB = new BufferedReader(
            new InputStreamReader(new FileInputStream(fp))) )
        {
           while ( (line = songDB.readLine()) != null && !line.equals(""))
           {    
               // Separate by delimiter
               String songElem [] = (line.split( "\\," ));

               //Assign elements to array
               SongDetails nextSong = new SongDetails( songElem[0].trim(), 
                   songElem[1].trim(), songElem[2].trim(),
                   songElem[3].trim(), songElem[4].trim(), 
                   songElem[5].trim() );
               songs.put( songElem[1].trim(), nextSong ); 
           }
        }
        
        // catches if song database doesn't exist
        catch ( FileNotFoundException exception )
        {
            Scanner response = new Scanner ( System.in );
            System.out.println( 
                "That database doesn't exist yet. Do you want to"
                + "create it? (Y/N)" );
            String createDB = response.nextLine().toUpperCase();
            switch ( createDB )
            {
                case "Y":
                    
                {
                    writeOut(); // writes empty database
                    break;
                }
                case "N":
                {
                    System.out.println( "Goodbye." );
                    System.exit(0);
                }
            }
            response.close(); 
        }
        // catches general IO exceptions
        catch (IOException exception )
        {
            System.out.println( "I/O Error: " + exception );
        }
    }
    
    /**
     * writeOut takes the data and writes it to a file based on the 
     * command line args. Called by readDBFile() and exitButton listener
     * @throws IOException caught if there are write issues
     */
    public void writeOut() throws IOException
    {
        try
        {
            BufferedWriter buffOut = new BufferedWriter(new FileWriter( filePath )); 
            // Loop through map entries and get value. 
            // Then write out with buffered writer
            for( Map.Entry<String, SongDetails> me : songs.entrySet() )
            {
                buffOut.write( me.getValue().toString() );
            }
            buffOut.close(); 
        }
        catch (IOException exception )
        {
            errorLabel.setText("Something went wrong. Changes not saved.");
        }
    }
}
