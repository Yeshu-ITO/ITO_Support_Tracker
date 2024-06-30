package ITO.Application.ItoSupportTracker.controller;


import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.service.UserService;
import com.marklogic.client.ResourceNotFoundException;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/addTicket")
    public ResponseEntity<Object> createTicket(@RequestBody Ticket ticket, @RequestParam Long userId) throws JAXBException {

        try {
            this.userService.createTicket(ticket, userId);
            return ResponseEntity.status(HttpStatus.OK).body("Ticket : " + ticket.getTicketId() + " Created Successfully.  "+ ticket.getTicketLink());
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }


    @GetMapping("/allTickets")
    public ResponseEntity<Object> getUserTickets(@RequestParam Long userId) throws JAXBException {
        try{
            return ResponseEntity.ok(this.userService.getAllTicketOfUser(userId));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/ticketId")
    public ResponseEntity<Object> getUserTicketById(@RequestParam Long userId, @RequestParam Long ticketId) throws JAXBException {
        try{
            return ResponseEntity.ok(this.userService.getTicketDetails(ticketId,userId));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping("/addComment")
    public ResponseEntity<Object> addCommentToUserTicket(@RequestBody TicketComment ticketComment, @RequestParam Long userId, @RequestParam Long ticketId) throws JAXBException {
        try{
            this.userService.addComment(ticketComment,ticketId,userId);
            return ResponseEntity.status(HttpStatus.OK).body("Comment added Successfully");
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
