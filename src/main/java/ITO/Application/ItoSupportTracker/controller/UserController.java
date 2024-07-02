package ITO.Application.ItoSupportTracker.controller;


import ITO.Application.ItoSupportTracker.model.Ticket;
import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.service.UserService;
import ITO.Application.ItoSupportTracker.service.adminService;
import com.marklogic.client.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private adminService adminService;

    @PostMapping("/addTicket")
    public ResponseEntity<Object> createTicket(@Valid @RequestBody Ticket ticket, @RequestParam Long userId) {
        try {
            this.userService.createTicket(ticket, userId);
            return ResponseEntity.
                    status(HttpStatus.OK).
                    body("Ticket : " + ticket.getTicketId() + " Created Successfully.  "+ ticket.getTicketLink());
        }
        catch (ResourceNotFoundException | JAXBException | NumberFormatException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/allTickets")
    public ResponseEntity<Object> getUserTickets(@RequestParam(value = "userId", defaultValue = "0",required = false) Long userId){
        try{
            if(userId != 0){
                return ResponseEntity.ok(userService.getAllTicketOfUser(userId));
            }else{
                return ResponseEntity.ok(adminService.getAllTickets());
            }
        }
        catch (ResourceNotFoundException | JAXBException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ticketId")
    public ResponseEntity<Object> getUserTicketById(@RequestParam Long userId, @RequestParam Long ticketId){
        try{
            return ResponseEntity.ok(this.userService.getTicketDetails(ticketId,userId));
        }
        catch (RuntimeException | JAXBException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/addComment")
    public ResponseEntity<Object> addCommentToUserTicket(@RequestBody TicketComment ticketComment, @RequestParam Long userId) throws JAXBException {
        try{
            this.userService.addComment(ticketComment,userId);
            return ResponseEntity.status(HttpStatus.OK).body("Comment added Successfully");
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
