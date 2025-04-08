<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${packagePath}.dal.dao.${simpleClassName}Mapper" >
  <resultMap id="BaseResultMap" type="${packagePath}.dal.po.${simpleClassName}" >
    <id column="id" property="id" />
    <result column="is_deleted" property="isDeleted" />
    <result column="create_time" property="createTime" />
    <result column="update_time" property="updateTime" />
    <result column="create_user" property="createUser" />
    <result column="update_user" property="updateUser" />
[#list fieldList as field]
[#if field.fieldType=='Enum']
    <result column="${field.fieldDb}" property="${field.fieldName}" typeHandler="${packagePath}.dal.base.IntegerAbleEnumHandler"/>
[#else]
    <result column="${field.fieldDb}" property="${field.fieldName}" />
[/#if]
[/#list]
  </resultMap>


  <sql id="Select_Column" >
   id
   ,is_deleted
   ,create_user
   ,update_user
   ,create_time
   ,update_time
    [#list fieldList as field]
	,${field.fieldDb}
	[/#list]
  </sql>


  <sql id="Insert_Column">
     create_time
     ,update_time
     , is_deleted
     ,create_user
     ,update_user
     [#list fieldList as field]
      ,${field.fieldDb}
     [/#list]
  </sql>

[#noparse]
<sql id="Insert_Value">
  #{po.createTime}
  ,#{po.updateTime}
  ,#{po.isDeleted}
  ,#{po.createUser}
  ,#{po.updateUser}
 [/#noparse]
 [#list fieldList as field]
    [#noparse],#{po.[/#noparse]${field.fieldName}[#noparse]}[/#noparse]
 [/#list]
</sql>


[#noparse]
 <sql id="Single_Where_Sql" >
    <if test="isDeleted != null" >
    ${joinStr} is_deleted = #{isDeleted}
    </if>
     <if test="createTime != null" >
    ${joinStr} create_time = #{createTime}
     </if>
     <if test="createUser != null" >
    ${joinStr} create_user = #{createUser}
     </if>
[/#noparse]
[#list fieldList as field]
[#if field.fieldType=='String']
     <if test="${field.fieldName} != null and ${field.fieldName} != ''" >
        [#noparse]${joinStr}[/#noparse] ${field.fieldDb} [#noparse]=#{[/#noparse]${field.fieldName}[#noparse]}[/#noparse]
     </if>
[#else]
     <if test="${field.fieldName} != null" >
         [#noparse]${joinStr}[/#noparse] ${field.fieldDb} [#noparse]=#{[/#noparse]${field.fieldName}[#noparse]}[/#noparse]
     </if>
[/#if]
[/#list]
</sql>

<sql id="Table_Name" >
   ${tableName}
</sql>


[#noparse]
      <sql id="Where_Sql" >
      <if test="endDate != null and endDate != ''">
      		<![CDATA[ and create_time < #{endDate} ]]>
      	</if>
      	<if test="startDate != null and startDate != ''">
      		<![CDATA[ and create_time >= #{startDate} ]]>
      	</if>
      	<if test="id != null" >
      		and id = #{id}
      	</if>
        <if test="ids != null and ids.size() > 0" >
          and
          <choose>
            <when test="queryListFieldName != null  and queryListFieldName !=''">${queryListFieldName}</when>
            <otherwise>id</otherwise>
          </choose>
          in
          <foreach collection="ids" item="item" open="(" separator="," close=")">
              #{item}
          </foreach>
        </if>
        <include refid="Single_Where_Sql"><property name="joinStr" value="and"/></include>
    </sql>


    <insert id="save" useGeneratedKeys="true" keyProperty="id">
        insert into
        <include refid="Table_Name"/>
        (
        <include refid="Insert_Column"/>
        )
        values (
        <include refid="Insert_Value"/>
        )
    </insert>


    <insert id="saveBatch">
        insert into
        <include refid="Table_Name"/>
        (
        <include refid="Insert_Column"/>
        )
        values
        <foreach item="po" collection="list" separator=",">
        (
          <include refid="Insert_Value"/>
        )
        </foreach>
    </insert>


  <select id="get" resultMap="BaseResultMap">
    select
    <include refid="Select_Column" />
    from <include refid="Table_Name" />
    where 1 = 1
    <include refid="Where_Sql"></include>
  </select>


  <select id="list" resultMap="BaseResultMap" >
    select
    <include refid="Select_Column" />
    from <include refid="Table_Name" /> 
    where 1 = 1
    <include refid="Where_Sql"></include>
    <if test="orderBy != null" >
    	order by ${orderBy}
    </if>
  </select>


  <select id="count" resultType="java.lang.Integer">
      select count(1) from <include refid="Table_Name" />
      where 1 = 1
      <include refid="Where_Sql"></include>
   </select>


  <delete id="remove">
      delete from <include refid="Table_Name" />
      where 1 = 1
      <include refid="Where_Sql"></include>
  </delete>


  <update id="edit">
    update <include refid="Table_Name" />
    <set >
       update_time = #{updateTime}
       <if test="updateParam != null" >
      <foreach collection="updateParam" index="k" item="v">
        <if test="null != v">
          , ${k} = #{v}
        </if>
      </foreach>
    </if>
    </set>
    <where>
    <include refid="Where_Sql"></include>
    </where>
    
  </update>

  <update id="logicRemove">
      update <include refid="Table_Name" />
      <set >
         update_time = #{updateTime},is_deleted = true
      </set>
      where 1 = 1
      <include refid="Where_Sql"></include>
   </update>
[/#noparse]


</mapper>