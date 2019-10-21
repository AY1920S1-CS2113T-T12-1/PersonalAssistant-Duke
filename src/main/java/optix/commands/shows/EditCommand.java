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

public class EditCommand extends Command {

    private static final String MESSAGE_UPDATE_SUCCESSFUL = "Show has been successfully updated to ";

    private static final String MESSAGE_UPDATE_UNSUCCESSFUL = "☹ OOPS!!! The show you are finding does not exist!\n";

    private String oldShowName;
    private String showDate;
    private String newShowName;

    private OptixDateFormatter formatter = new OptixDateFormatter();

    /**
     * Edit the name of an existing show.
     *
     * @param splitStr String of format "OLD_SHOW_NAME|SHOW_DATE|NEW_SHOW_NAME"
     */
    public EditCommand(String splitStr) throws OptixInvalidCommandException {
        String[] details = parseDetails(splitStr);
        if (details.length != 3) {
            throw new OptixInvalidCommandException();
        }
        this.oldShowName = details[0].trim();
        this.showDate = details[1].trim();
        this.newShowName = details[2].trim();
    }

    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ShowMap shows = model.getShows();
        String message;
        try {
            if (!formatter.isValidDate(showDate)) {
                throw new OptixInvalidDateException();
            }
            LocalDate localShowDate = formatter.toLocalDate(showDate);

            if (shows.containsKey(localShowDate) && shows.get(localShowDate).hasSameName(oldShowName)) {
                Theatre show = shows.get(localShowDate);
                show.setShowName(newShowName);

                shows.replace(localShowDate, show);
                model.setShows(shows);
                message = MESSAGE_UPDATE_SUCCESSFUL + newShowName + ".\n";
            } else {
                message = MESSAGE_UPDATE_UNSUCCESSFUL;
            }
            ui.setMessage(message);
        } catch (OptixInvalidDateException e) {
            ui.setMessage(e.getMessage());
        }
    }

    @Override
    public String[] parseDetails(String details) {
        return details.split("\\|");
    }

    @Override
    public boolean isExit() {
        return super.isExit();
    }
}
