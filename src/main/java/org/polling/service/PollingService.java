package org.polling.service;

/**
 * Polling is done by user where user can invite his/her connected users for
 * polling. User can poll a question with multiple choice options and invited
 * users can post the result on selected poll. Result of particular poll will
 * displayed to only user of poll creation. <code>PollingService</code> is an
 * interface which contains all the functionality related to Poll.
 * 
 * @author pranav.arya
 */
public interface PollingService {

    /**
     * This method will contains creation and edition of poll. On the basis of
     * variable flag it will decide whether the request is for save or for edit.
     * Firstly, it will check whether the pollId is null or not. If it is null
     * then, new poll is created . If pollId is not null then it will check flag
     * variable whether it contains "edit" or not, if flag is "edit" then poll
     * is edited .
     * 
     * 
     * @param userId
     *            Contains the userId for poll creation which is unique
     * @param pollId
     *            Contains the pollId which is unique for respective user
     * @param flag
     *            Contains the flag for edit or save
     * @param title
     *            Contains the Title Question of the Poll.
     * @param fromDate
     *            Contains the start date of the Poll.
     * @param toDate
     *            Contains the end date of the Poll.
     * @param choices
     *            Contains the choices of the Poll as an array.
     * @return the string containing the PollId
     */
    String createPoll(String userId, String pollId, String flag, String title,
            String fromDate, String toDate, String[] choices);

    /**
     * This method is used to add the participants for particular poll
     *
     * @param userId
     *            Contains the userId which is unique
     * @param pollId
     *            Contains the pollId which is unique for respective user
     * @param participent
     *            contains the userId of participant
     */
    void addParticipent(String userId, String pollId, String participent);

    /**
     * This method is used to sets the result for particular pollId. Choice for
     * particular user is saved.
     * 
     * @param userId
     *            Contains the userId which is unique
     * @param pollId
     *            Contains the pollId which is unique for respective user
     * @param choice
     *            Contains the choice of particular user
     */
    void setResult(String userId, String pollId, String choice);

    /**
     * Checks whether the user Node contains the poll node.If Poll node is not
     * present then poll node is created in respective userId.
     * 
     * @param userID
     *            Contains the userId which is unique
     */
    void checkPollNode(String userID);

    /**
     * On the basis of pollPath, respective poll is deleted.
     * 
     * @param pollPath
     *            contains the full poll path
     */
    void deletePoll(String pollPath);
}
