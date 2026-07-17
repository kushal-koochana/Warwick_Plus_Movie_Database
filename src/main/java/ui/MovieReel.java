package ui;

import javax.swing.JPanel;

import screen.FilmScreen;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import interfaces.AbstractStores;

public class MovieReel extends AbstractReel{
    protected AbstractStores stores;

    public MovieReel(JPanel screenPanel, AbstractStores stores,
                     String reelTitle, String loadingMessage)
    {
        super(screenPanel, reelTitle, loadingMessage);
        this.stores = stores;

    }
    
    public MovieReel(JPanel screenPanel, AbstractStores stores)
    {
        super(screenPanel, "Movie Reel", "Loading Movie Reel");
        this.stores = stores;
    }

    @Override
    protected Image getImage(int movieId){
        try{
            String posterEndURL = stores.getMovies().getPoster(movieId);
            Image unknown = ImageIO.read(new File("src/main/resources/img/Movie-Unknown-poster.png"));
            if (posterEndURL == null || posterEndURL.equals("")){
                return unknown;
            }

            String completeURL = "https://image.tmdb.org/t/p/w342" + posterEndURL;
            Image im;
            try{
                im = ImageIO.read(new URL(completeURL));
                if (im == null){
                    im = unknown;
                }
            }
            catch (IOException e){
                im = unknown;
            }

            return im;

        }
        catch (IOException e){
            return null;

        }
        
    }

    @Override
    protected void itemClickAction(int movieId) {
        FilmScreen.createPanel(super.screenPanel, movieId, stores);
    }
}
