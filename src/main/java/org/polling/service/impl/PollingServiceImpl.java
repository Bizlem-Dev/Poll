package org.polling.service.impl;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.polling.service.PollingService;

/**
 * Poll is a kind of survey which gathers the opinion by different users.
 * <code>PollingServiceImpl</code> is an implementation class which contains all
 * functionality related to Poll. It contains the functionality like: poll
 * creation, poll deletion, invite participants, save result.
 * 
 * @author pranav.arya
 * 
 */
@Component(configurationFactory = true)
@Service(PollingService.class)
@Properties({ @Property(name = "PollingService", value = "polling") })
public class PollingServiceImpl implements PollingService {

    /**
     * Object declaration for SlingRepository
     */
    @Reference
    private SlingRepository repo;

    /*
     * (non-Javadoc)
     * 
     * @see org.polling.service.PollingService#createPoll(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String[])
     */
    @SuppressWarnings("unused")
    public String createPoll(String userId, String pollId, String flag,
            String title, String fromDate, String toDate, String[] choices) {

        Session session;
        Node pollIdNode = null, choiceNode, optionNode;
        Node userNode, pollNode;
        try {

            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));
            if (pollId == null) {
                pollIdNode = session.getNode(getNode(userId, "null"));
            } else {
                pollIdNode = session.getNode(getNode(userId, pollId));
                if (flag.equals("delete")) {
                    pollIdNode.remove();
                    return null;
                } else if (flag.equals("edit")) {
                    choiceNode = pollIdNode.getNode("Choices");
                    choiceNode.remove();
                    choiceNode = null;
                }
            }

            pollIdNode.setProperty("pollTitle", title);
            pollIdNode.setProperty("pollStartDate", fromDate);
            pollIdNode.setProperty("pollEndDate", toDate);
            // pollIdNode.setProperty("pollChoices", choices);
            if (pollIdNode.hasNode("Choices")) {
                choiceNode = pollIdNode.getNode("Choices");
            } else {
                choiceNode = pollIdNode.addNode("Choices");
            }

            for (int i = 0; i < choices.length; i++) {
                optionNode = choiceNode.addNode((i + 1) + "");

                optionNode.setProperty("choiceName", choices[i]);
            }

            session.save();
            return pollIdNode.getName();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.polling.service.PollingService#addParticipent(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
    public void addParticipent(String userId, String pollId, String participent) {
        Session session;
        Node pollIdNode, participentNode, participantNode, userNode,
        pollNode, requestNode, userRequestNode, userPollNode;

        try {
            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));
            pollIdNode = session.getNode(getNode(userId, pollId));

            if (pollIdNode.hasNode("Participent")) {
                participentNode = pollIdNode.getNode("Participent");
            } else {
                participentNode = pollIdNode.addNode("Participent");
            }

            for (String participant : participent.split(",")) {

                if (participentNode.hasNode(participant)) {
                    participantNode = participentNode.getNode(participant);
                } else {
                    participantNode = participentNode.addNode(participant);
                }
                if (session.itemExists("/content/user/" + participant)) {
                    userNode = session.getNode("/content/user/" + participant);

                    if (userNode.hasNode("Poll")) {
                        pollNode = userNode.getNode("Poll");
                    } else {
                        pollNode = userNode.addNode("Poll");
                        pollNode.setProperty("sling:resourceType", "poll");
                        pollNode.setProperty("pollNumber", "0");
                    }

                    if (pollNode.hasNode("PollRequest")) {
                        requestNode = pollNode.getNode("PollRequest");
                    } else {
                        requestNode = pollNode.addNode("PollRequest");
                    }

                    if (requestNode.hasNode(userId)) {
                        userRequestNode = requestNode.getNode(userId);
                    } else {
                        userRequestNode = requestNode.addNode(userId);
                    }

                    if (userRequestNode.hasNode(pollIdNode.getName())) {
                        userPollNode = userRequestNode.getNode(pollIdNode
                                .getName());
                    } else {
                        userPollNode = userRequestNode.addNode(pollIdNode
                                .getName());
                        userPollNode.setProperty("pollPath",
                                pollIdNode.getPath());
                    }
                }
            }
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the full poll path on the basis of userId and
     * pollId. If the pollId is present, then full path of poll is return
     * otherwise poll is created.
     * 
     * @param userId
     *            Contains the userId which is unique
     * @param pollId
     *            Contains the pollId which is unique for respective user
     * @return the string which contains the full JCR poll path
     */
    private String getNode(String userId, String pollId) {

        Session session;
        Node userNode, pollNode, pollIdNode = null;
        try {
            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));
            userNode = session.getNode("/content/user/" + userId);
            if (userNode.hasNode("Poll")) {
                pollNode = userNode.getNode("Poll");
            } else {
                pollNode = userNode.addNode("Poll");
                pollNode.setProperty("sling:resourceType", "poll");
                pollNode.setProperty("pollNumber", "0");
            }
            if (!pollId.equals("null") && pollNode.hasNode(pollId)) {
                pollIdNode = pollNode.getNode(pollId);
            } else {
                pollIdNode = pollNode.addNode("Poll"
                        + pollNode.getProperty("pollNumber").getString());
                if (pollNode.hasProperty("pollNumber")) {
                    pollNode.setProperty("pollNumber",
                            pollNode.getProperty("pollNumber").getLong() + 1);
                }
            }
            session.save();
            return pollIdNode.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.polling.service.PollingService#setResult(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
    public void setResult(String userId, String pollpath, String choice) {

        Session session;
        Node pollIdNode, choiceNode = null, resultNode, userNode,
                userPollNode, pollRequestNode;
        try {
            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));

            pollIdNode = session.getNode(pollpath);

            if (pollIdNode.hasNode("Choices")
                    && pollIdNode.getNode("Choices").hasNodes()) {
                choiceNode = pollIdNode.getNode("Choices").getNode(choice);
            }

            if (choiceNode.hasNode("Result")) {
                resultNode = choiceNode.getNode("Result");
            } else {
                resultNode = choiceNode.addNode("Result");
            }

            if (resultNode.hasNode(userId)) {
                userNode = resultNode.getNode(userId);
            } else {
                userNode = resultNode.addNode(userId);
            }

            userPollNode = session.getNode("/content/user/" + userId);

            if (userPollNode.hasNode("Poll")
                    && userPollNode.getNode("Poll").hasNode("PollRequest")
                    && userPollNode
                            .getNode("Poll")
                            .getNode("PollRequest")
                            .hasNode(
                                    pollIdNode.getParent().getParent()
                                            .getName())
                    && userPollNode
                            .getNode("Poll")
                            .getNode("PollRequest")
                            .getNode(
                                    pollIdNode.getParent().getParent()
                                            .getName())
                            .hasNode(pollIdNode.getName())) {
                pollRequestNode = userPollNode.getNode("Poll")
                        .getNode("PollRequest")
                        .getNode(pollIdNode.getParent().getParent().getName())
                        .getNode(pollIdNode.getName());
                pollRequestNode.remove();
            }
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.polling.service.PollingService#checkPollNode(java.lang.String)
     */
    public void checkPollNode(String userID) {
        Session session = null;
        Node pollNode;
        try {
            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));
            if (session.getNode("/content/user/" + userID).hasNode("Poll")) {

            } else {
                pollNode = session.getNode("/content/user/" + userID).addNode(
                        "Poll");
                pollNode.setProperty("sling:resourceType", "poll");
                pollNode.setProperty("pollNumber", "0");
            }
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.logout();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.polling.service.PollingService#deletePoll(java.lang.String)
     */
    public void deletePoll(String pollPath) {
        Session session = null;
        Node pollNode;
        try {
            session = repo.login(new SimpleCredentials("admin", "admin"
                    .toCharArray()));

            pollNode = session.getNode(pollPath);
            pollNode.remove();
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.logout();
        }

    }
}
