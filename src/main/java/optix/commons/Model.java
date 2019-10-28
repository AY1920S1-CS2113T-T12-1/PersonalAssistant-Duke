package optix.commons;

import optix.commons.model.ShowMap;
import optix.commons.model.Theatre;
import optix.util.OptixDateFormatter;

import java.time.LocalDate;
import java.util.Map;

public class Model {
    private ShowMap showsHistory = new ShowMap();
    private ShowMap shows = new ShowMap();
    private ShowMap showsGUI;

    /**
     * The Optix model.
     * @param storage the object which handles data from the save file.
     */
    public Model(Storage storage) {
        storage.loadShows(shows, showsHistory);
        storage.loadArchive(showsHistory);
        storage.writeArchive(showsHistory);
        showsGUI = this.getShows();
    }

    public ShowMap getShows() {
        return shows;
    }

    public ShowMap getShowsHistory() {
        return showsHistory;
    }

    public ShowMap getShowsGUI() {
        return showsGUI;
    }

    public void setShowsGUI(ShowMap showsGUI) {
        this.showsGUI = showsGUI;
    }

    public boolean hasSameName(LocalDate key, String showName) {
        return shows.get(key).hasSameName(showName);
    }

    public boolean containsKey(LocalDate key) {
        return shows.containsKey(key);
    }

    //// Commands that deals with Shows

    public void addShow(String showName, LocalDate showDate, double seatBasePrice) {
        shows.addShow(showName, showDate, seatBasePrice);
        this.setShowsGUI(shows);
    }

    public void editShowName(LocalDate showDate, String showName) {
        shows.editShowName(showDate, showName);
        this.setShowsGUI(shows);
    }

    public void postponeShow(LocalDate oldDate, LocalDate newDate) {
        shows.postponeShow(oldDate, newDate);
        this.setShowsGUI(shows);
    }

    public String listShow() {
        this.setShowsGUI(shows);
        return shows.listShow();
    }

    public String listShow(String showName) {
        this.setShowsGUI(shows.listShow(showName));
        StringBuilder message = new StringBuilder();
        int counter = 1;
        for (Map.Entry<LocalDate, Theatre> entry : showsGUI.entrySet()) {
            String date = new OptixDateFormatter().toStringDate(entry.getKey());
            message.append(String.format("%d. %s\n", counter, date));
            counter++;
        }
        return message.toString();
    }

    public String listShow(LocalDate startOfMonth, LocalDate endOfMonth) {
        this.setShowsGUI(shows.listShow(startOfMonth, endOfMonth));
        StringBuilder message = new StringBuilder();
        int counter = 1;
        for (Map.Entry<LocalDate, Theatre> entry : showsGUI.entrySet()) {
            String date = new OptixDateFormatter().toStringDate(entry.getKey());
            String showName = entry.getValue().getShowName();
            message.append(String.format("%d. %s (on: %s)\n", counter, showName, date));
            counter++;
        }
        return message.toString();
    }

    public void deleteShow(LocalDate showDate) {
        shows.deleteShow(showDate);
        this.setShowsGUI(shows);
    }

    //// Commands that deals with Seats.

    public String viewSeats(LocalDate localDate) {
        return shows.viewSeats(localDate);
    }

    public String sellSeats(LocalDate localDate, String... seats) {
        return shows.sellSeats(localDate, seats);
    }

    public String reassignSeat(LocalDate showlocalDate, String oldSeat, String newSeat) {
        return shows.reassignSeat(showlocalDate, oldSeat, newSeat);
    }

}
