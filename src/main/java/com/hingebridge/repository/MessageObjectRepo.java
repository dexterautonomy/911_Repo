package com.hingebridge.repository;

import com.hingebridge.model.MessageObject;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageObjectRepo extends JpaRepository<MessageObject, Long>
{
    /*
    @Query("SELECT mo FROM MessageObject mo WHERE mo.recipient_id = :user_id AND mo.flag = 1 ORDER BY mo.id DESC")
    public Page<MessageObject> getMyMessage(@Param("user_id")Long user_id, Pageable pageable);
    */
    
    @Query("SELECT mo FROM MessageObject mo WHERE mo.recipient_id = :user_id AND mo.flag = 1 ORDER BY mo.id DESC")
    public List<MessageObject> getMyMessage(@Param("user_id")Long user_id);
    
    /*implementation here
    public default Page<MessageObject> getMessages(Long user_id, Pageable pageable)
    {
        Page<MessageObject> getMessages = getMyMessage(user_id, pageable);
        List<MessageObject> messages = new LinkedList<>();
        
        if(!getMessages.isEmpty())
        {
            for(MessageObject m : getMessages)
            {
                if(m.getRecipient_id().equals(user_id))
                {
                    messages.add(m);
                }
            }
            getMessages = new PageImpl<>(messages, pageable, getMessages.getTotalElements());
        }
        else
        {
            getMessages = null;   //No messages, work it out
        }
        return getMessages;
    }
    */
}
