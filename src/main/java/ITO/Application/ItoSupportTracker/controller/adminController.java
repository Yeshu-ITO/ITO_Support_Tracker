package ITO.Application.ItoSupportTracker.controller;


import ITO.Application.ItoSupportTracker.model.TicketComment;
import ITO.Application.ItoSupportTracker.service.UserService;
import ITO.Application.ItoSupportTracker.service.adminService;
import ITO.Application.ItoSupportTracker.utility.Constants;
import com.marklogic.client.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/api/admin")
@Validated
public class adminController {

    @Autowired
    private adminService adminService;

    @Autowired
    private UserService userService;


    @GetMapping("/allTickets")
    public ResponseEntity<Object> getAllTickets(@RequestParam(value = "userId", defaultValue = "0",required = false) Long userId,
                                                @RequestParam(value = "assigneeId", defaultValue = "0",required = false) Long assigneeId)
    {
        try{
            if(userId != 0 && assigneeId != 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide Either User ID or Assignee Id");
            }else if(userId != 0){
                return ResponseEntity.ok(userService.getAllTicketOfUser(userId));
            }else if(assigneeId != 0){
                return ResponseEntity.ok(adminService.getAssigneeTickets(assigneeId));
            }else {
                return ResponseEntity.ok(adminService.getAllTickets());
            }
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
    public ResponseEntity<Object> addCommentToUserTicket(@Valid @RequestBody TicketComment ticketComment, @RequestParam Long adminId) {
        try{
            this.adminService.addAdminComment(ticketComment,adminId);
            return ResponseEntity.status(HttpStatus.OK).body("Comment added Successfully");
        }
        catch (RuntimeException | JAXBException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/byDate")
    public ResponseEntity<Object> getTicketsByDate(@RequestParam String date) {
        try{
            return ResponseEntity.ok(this.adminService.getTicketsByDate(date));
        }catch (JAXBException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
