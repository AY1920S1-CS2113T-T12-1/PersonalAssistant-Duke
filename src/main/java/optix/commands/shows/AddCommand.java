package optix.commands.shows;

import optix.commons.Model;
import optix.exceptions.OptixInvalidCommandException;
import optix.ui.Ui;
import optix.commands.Command;
import optix.commons.Storage;
import optix.commons.model.Theatre;
import optix.exceptions.OptixInvalidDateException;
import optix.util.OptixDateFormatter;
import optix.commons.model.ShowMap;

import java.time.LocalDate;

public class AddCommand extends Command {
    private String showName;
    private String date;
    private double seatBasePrice;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    private static final String MESSAGE_IN_THE_PAST = "☹ OOPS!!! It is not possible to perform in the past.\n";

    private static final String MESSAGE_THEATRE_BOOKED = "☹ OOPS!!! There is already a show being added on that date.\n"
            + "Please try again. \n";

    private static final String MESSAGE_SUCCESSFUL = "Got it. I've added this show:\n"
            + "%1$s on %2$s\n";

    /**
     * Add a show to the show list.
     *
     * @param splitStr String of format "SHOW_NAME|SHOW_DATE|SEAT_BASE_PRICE"
     */
    public AddCommand(String splitStr) throws OptixInvalidCommandException {
        String[] details = parseDetails(splitStr);
        if (details.length != 3) {
            throw new OptixInvalidCommandException();
        }
        // need to check if it is a valid date if not need to throw exception
        this.showName = details[0].trim();
        this.date = details[1].trim();
        this.seatBasePrice = Double.parseDouble(details[2]);
    }

    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ShowMap shows = model.getShows();
        Theatre theatre = new Theatre(showName, seatBasePrice);
        LocalDate today = storage.getToday();

        try {
            if (!formatter.isValidDate(date)) {
                throw new OptixInvalidDateException();
            }

            LocalDate showLocalDate = formatter.toLocalDate(date);

            if (showLocalDate.compareTo(today) <= 0) {
                ui.setMessage(MESSAGE_IN_THE_PAST);
            } else if (shows.containsKey(showLocalDate)) {
                ui.setMessage(MESSAGE_THEATRE_BOOKED);
            } else {
                shows.put(showLocalDate, theatre);
                model.setShows(shows);
                ui.setMessage(String.format(MESSAGE_SUCCESSFUL, theatre.getShowName(), date));
            }
        } catch (OptixInvalidDateException e) {
            ui.setMessage(e.getMessage());
        }
    }

    @Override
    public String[] parseDetails(String details) {
        return details.trim().split("\\|", 3);
    }


    @Override
    public boolean isExit() {
        return super.isExit();
    }
}
