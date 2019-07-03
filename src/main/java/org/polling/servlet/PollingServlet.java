package org.polling.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.polling.service.PollingService;

/**
 * <code>PollingServlet</code> is a servlet which contains all the request and
 * responses related to Poll.
 * 
 * @author pranav.arya
 * 
 */
@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
        @Property(name = "service.description",
                value = "Prefix Test Servlet Minus One"),
        @Property(name = "service.vendor",
                value = "The Apache Software Foundation"),
        @Property(name = "sling.servlet.paths",
                value = { "/servlet/poll/show" }),
        @Property(name = "sling.servlet.extensions", value = { "save", "poll",
                "view", "viewResult", "viewEdit", "viewSave", "edit",
                "viewPoll", "deletePoll" })

})
@SuppressWarnings("serial")
public class PollingServlet extends SlingAllMethodsServlet {

    /** Object declaration of <code>SlingRepository</code> */
    @Reference
    private SlingRepository repos;

    /** Object declaration for <code>PollingService</code> */
    @Reference
    private PollingService service;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache
     * .sling.api.SlingHttpServletRequest,
     * org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        if (request.getRequestPathInfo().getExtension().equals("save")) {
            String pollId = "";
            pollId = service.createPoll(request.getParameter("userId"), null,
                    "", request.getParameter("title"),
                    request.getParameter("startDate"),
                    request.getParameter("endDate"),
                    request.getParameterValues("choice"));
            if (request.getParameter("participent") != "") {
                service.addParticipent(request.getParameter("userId"), pollId,
                        request.getParameter("participent"));
            }
            response.sendRedirect(request.getContextPath()
                    + "/servlet/poll/show.poll?userId="
                    + request.getParameter("userId"));
        } else if (request.getRequestPathInfo().getExtension().equals("edit")) {
            String pollId = "";
            pollId = service.createPoll(request.getParameter("userId"),
                    request.getParameter("pollId"), "edit",
                    request.getParameter("title"),
                    request.getParameter("startDate"),
                    request.getParameter("endDate"),
                    request.getParameterValues("choice"));
            if (request.getParameter("participent") != "") {
                service.addParticipent(request.getParameter("userId"), pollId,
                        request.getParameter("participent"));
            }
            response.sendRedirect(request.getContextPath()
                    + "/servlet/poll/show.poll?userId="
                    + request.getParameter("userId"));
        } else if (request.getRequestPathInfo().getExtension()
                .equals("invite")) {
            service.addParticipent(request.getParameter("userId"),
                    request.getParameter("pollId"),
                    request.getParameter("participent"));
            response.sendRedirect(request.getContextPath()
                    + "/servlet/poll/show.poll?userId="
                    + request.getParameter("userId"));

        } else if (request.getRequestPathInfo().getExtension()
                .equals("result")) {

            service.setResult(request.getParameter("userId"),
                    request.getParameter("pollPath"),
                    request.getParameter("choiceResult"));
        } else if (request.getRequestPathInfo().getExtension()
                .equals("deletePoll")) {

            service.deletePoll("/content/user/"
                    + request.getParameter("userId") + "/Poll/"
                    + request.getParameter("poll"));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache
     * .sling.api.SlingHttpServletRequest,
     * org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        if (request.getRequestPathInfo().getExtension().equals("view")) {
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.listPoll").forward(request, response);
        } else if (request.getRequestPathInfo().getExtension()
                .equals("viewSave")) {
            request.getRequestDispatcher("/content/poll/*.editPoll").forward(
                    request, response);

        } else if (request.getRequestPathInfo().getExtension()
                .equals("viewEdit")) {
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.editPoll").forward(request, response);
        } else if (request.getRequestPathInfo().getExtension()
                .equals("viewResult")) {
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.result").forward(request, response);
        } else if (request.getRequestPathInfo().getExtension()
                .equals("viewPoll")) {
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.viewPoll").forward(request, response);
        } else if (request.getRequestPathInfo().getExtension().equals("poll")) {
            service.checkPollNode(request.getParameter("userId"));
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.poll").forward(request, response);

        } else if (request.getRequestPathInfo().getExtension()
                .equals("inviteUser")) {
            request.getRequestDispatcher(
                    "/content/user/" + request.getParameter("userId")
                            + "/Poll.invite").forward(request, response);

        }
    }
}
