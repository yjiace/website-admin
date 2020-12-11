package cn.smallyoung.websiteadmin.controller.sys;

import cn.hutool.core.collection.CollUtil;
import cn.smallyoung.websiteadmin.entity.sys.MessageNotification;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.sys.MessageNotificationService;
import cn.smallyoung.websiteadmin.service.sys.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author smallyoung
 * @data 2020/11/14
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/message/notification")
public class MessageNotificationController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private MessageNotificationService messageNotificationService;

    /**
     * 查询未读信息总数
     */
    @GetMapping(value = "findMessageCount")
    public Integer findMessageCount(){
        return messageNotificationService.unreadCount(sysUserService.currentlyLoggedInUser());
    }
    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @PreAuthorize("hasRole('ROLE_MESSAGE_NOTIFICATION') or hasRole('ROLE_MESSAGE_NOTIFICATION_FIND')")
    public Page<MessageNotification> findAll(@RequestParam(defaultValue = "1") Integer page,
                                             HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return messageNotificationService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "status", "createTime")));
    }

    /**
     * 分页查询自己的消息
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findYourOwnMessageNotification")
    public Page<MessageNotification> findYourOwnMessageNotification(@RequestParam(defaultValue = "1") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("AND_EQ_recipientUsername", sysUserService.currentlyLoggedInUser());
        map.put("AND_EQ_isDelete", "N");
        return messageNotificationService.findAll(map,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "status", "createTime")));
    }

    /**
     * 删除消息
     *
     * @param ids 主键ID
     */
    @DeleteMapping(value = "deleteMessageNotification")
    public List<MessageNotification> deleteMessageNotification(@RequestParam(value = "ids") List<String> ids) {
        if(CollUtil.isEmpty(ids)){
            throw new NullPointerException("参数错误");
        }
        List<MessageNotification> messageNotifications = messageNotificationService.findByIdIn(ids);
        if(CollUtil.isNotEmpty(messageNotifications)){
            messageNotifications.forEach(m -> m.setIsDelete("Y"));
            messageNotificationService.save(messageNotifications);
        }
        return messageNotifications;
    }


    /**
     * 阅读消息
     *
     * @param ids 主键ID列表
     */
    @PostMapping(value = "readingMessageNotification")
    public List<MessageNotification> readingMessageNotification(@RequestParam(value = "ids") List<String> ids) {
        if(CollUtil.isEmpty(ids)){
            throw new NullPointerException("参数错误");
        }
        List<MessageNotification> messageNotifications = messageNotificationService.findByIdIn(ids);
        if(CollUtil.isNotEmpty(messageNotifications)){
            LocalDateTime now = LocalDateTime.now();
            messageNotifications.forEach(m -> {
                m.setStatus("Read");
                m.setReadingTme(now);
            });
            messageNotificationService.save(messageNotifications);
        }
        return messageNotifications;
    }
}
