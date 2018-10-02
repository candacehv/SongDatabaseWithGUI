
/**
 * This class holds the details of each song, and creates a type
 * SongDetails for each database entry which includes 
 * song title, item code, description, artist, album, and price.
 * 
 * The item code is used as the TreeMap key. 
 * 
 * @author Candace Holcombe-Volke
 *
 */
import java.util.Scanner;


public class SongDetails 
{
    // holds new song details temporarily to be added to TreeMap
    SongDetails detailsArr[] = new SongDetails[6]; 
    private String songTitle; 
    private String itemCode; 
    private String description; 
    private String artist; 
    private String album; 
    private double price; 
    Scanner scan = new Scanner( System.in );

    /**
     * Override the default toString()
     */
    public String toString()
    {
       return songTitle + "," + itemCode + "," + description + ","
           + artist + "," + album + "," + price + ",\n"; 
    }
    
    //Constructor for all components of ContactDetails objects
    // with 5 String arguments, used by readDBFile()
    /**
     * Constructor for all components of ContactDetails objects
     * with 5 String arguments, used by readDBFile()
     * Price is entered as String and parsed to Double
     * @param song song title
     * @param code item code
     * @param desc description
     * @param art artist
     * @param alb album
     * @param pr price
     */
    public SongDetails( String song, String code, String desc, 
        String art, String alb, String pr ) 
    {
        songTitle = song; 
        itemCode = code;
        description = desc;
        artist = art; 
        album = alb; 
        price = Double.parseDouble(pr);
    }
    
    
    // Constructor for start up
    public SongDetails ( String songElement[] )
    {
        songTitle = songElement[0]; 
        itemCode = songElement[1];
        description = songElement[2];
        artist = songElement[3]; 
        album = songElement[4]; 
        price = Double.parseDouble( songElement[5] );
    }
    

    //Set methods below:
    public void setSongTitle( String code )
    {
        songTitle = code;
    } 
    
    public void setItemCode( String item)
    {
        itemCode = item;
    } 
    
    public void setDescription( String desc)
    {
        description = desc;
    }
    

    public void setArtist( String art)
    {
        artist = art;
    }
    
    public void setAlbum( String alb)
    {
        album = alb;
    }
    
    public void setPrice( double pr )
    {
        price = pr; 
    }
    
    
    // get methods below
    /*
     * @return songTitle
     */
    public String getSongTitle()
    {
        return songTitle;
    }
    
    /**
     * 
     * @return itemCode
     */
    public String getItemCode()
    {
        return itemCode;
    }
    
    /**
     * 
     * @return description text
     */
    public String getDescription()
    {
       return description;
    }
    
    /**
     * 
     * @return artist text
     */
    public String getArtist()
    {
        return artist;
    } 
    
    /**
     * 
     * @return album name text
     */
    public String getAlbum()
    {
        return album;
    }

    /**
     * 
     * @return price as a string
     */
    public String getPriceString()
    {
        return String.valueOf( price );
    }
    
    /**
     * 
     * @return price as a double
     */
    public double getPriceDouble()
    {
        return price;
    }
    
    SongDetails()
    {
        // no op
    }
}
