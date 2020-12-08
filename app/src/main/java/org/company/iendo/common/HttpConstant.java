package org.company.iendo.common;

/**
 * LoveLin
 * <p>
 * Describe http请求用到的常量
 */
public class HttpConstant {
    // http://192.168.128.146:8009/  users.aspx

    /**
     * 登录 get
     */
    public static final String Login = "users.aspx";


    /**
     * 病例管理
     */
    public static final String CaseManager_List = "patients_list.aspx";
    public static final String CaseManager_Case_Detail = "patients.aspx";
    public static final String CaseManager_Case_Edit = "listdicts.aspx";
    public static final String CaseManager_Live_Connect = "add_command.aspx";     //post
    public static final String CaseManager_Live_Connect_GetID = "get_command_id.aspx";     //get
    public static final String CaseManager_Live_Connect_Delete = "delete_patients.aspx";     //get
    public static final String CaseManager_Add_GetCaseID = "caseno.aspx";     //post
    public static final String CaseManager_Add_Patients = "add_patients.aspx";     //post     //add_patients.aspx    添加病例信息
    public static final String CaseManager_Update_Patients = "update_patients.aspx";     //post      update_patients.aspx  更新指定的病例信息
    public static final String CaseManager_Get_LastNo = "last_patients.aspx";     //获取最后一条病例的，检查号



}



