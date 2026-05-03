package com.jiangtao.smsbilling.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangtao.smsbilling.entity.SmsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SmsRecordMapper extends BaseMapper<SmsRecord> {
    @Select("SELECT app_id, COUNT(*) as total_count, SUM(total_fee) as total_fee, " +
            "SUM(CASE WHEN status=1 THEN 1 ELSE 0 END) as success_count " +
            "FROM sms_record GROUP BY app_id")
    List<Map<String, Object>> statByApp();

    @Select("SELECT DATE_FORMAT(create_time, #{fmt}) as time_key, " +
            "COUNT(*) as total_count, SUM(total_fee) as total_fee " +
            "FROM sms_record GROUP BY time_key ORDER BY time_key")
    List<Map<String, Object>> statByTime(@Param("fmt") String fmt);
}
