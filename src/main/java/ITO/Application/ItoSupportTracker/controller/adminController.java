package ITO.Application.ItoSupportTracker.controller;


import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.repository.adminRepository;
import ITO.Application.ItoSupportTracker.service.adminService;
import ITO.Application.ItoSupportTracker.utility.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.marklogic.client.ResourceNotFoundException;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class adminController {

    @Autowired
    private adminService adminService;


    @GetMapping("/allTickets")
    public ResponseEntity<Object> getAllTickets() {
        try{
            return ResponseEntity.ok(adminService.getAllTickets());
        }
        catch (ResourceNotFoundException | JAXBException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/getTicketDetails")
    public ResponseEntity<Object> getTicketDetails(@RequestParam Long ticketId) {
        try{
            return  ResponseEntity.ok(adminService.getTicketDetails(ticketId));
        }
        catch (ResourceNotFoundException | JAXBException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping("/setAssignee")
    public ResponseEntity<String> setAssignee(@RequestParam Long ticketId, @RequestParam Long assigneeId){
        try{
            adminService.setAssignee(ticketId,assigneeId);
            return ResponseEntity.status(HttpStatus.OK).body("Assignee Id as been set successfully for the Ticket : " + ticketId);
        }
        catch (ResourceNotFoundException | JAXBException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping("/changeStatus")
    public ResponseEntity<Object> changeStatus(@RequestParam Long ticketId, @RequestParam Long userId, @RequestParam Long statusId){

        try{
            String oldStatus = adminService.changeStatus(ticketId,userId,statusId);
            return ResponseEntity.status(HttpStatus.OK).body("Status as been changed from " + oldStatus + " to " + Constants.Status.values()[Math.toIntExact(statusId)-1]);
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }


    @PostMapping("/addComment")
    public ResponseEntity<Object> addCommentToUserTicket(@RequestBody TicketComment ticketComment, @RequestParam Long adminId, @RequestParam Long ticketId) {
        try{
            this.adminService.addAdminComment(ticketComment,adminId,ticketId);
            return ResponseEntity.status(HttpStatus.OK).body("Comment added Successfully");
        }
        catch (RuntimeException | JAXBException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
