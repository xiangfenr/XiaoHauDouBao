package com.yundou.loans.entity;


import java.io.Serializable;

public class UserInfoData implements Serializable {

    public UserInfoData() {

    }

    public UserInfoData(String id, String name, String isZjgongan, String idNumber, String nativePlace, String sex, String nation, String mechanism, String idCardStartTime, String idCardEndTime, String idImg, String idBackImg, String sitePhotos, String phone, String age, String workerNo, String companyId, String froemanName, String companyName, String projectName, String politics, String educationType, String marriage, String bankName, String bankCardNo, String unionPayAccount, String joinGuildTime, String emergencyContact, String emergencyPhone, String attendanceNo, String majorDiseases, String joinGuild, String isContract, String shtick, String isFree, String isBuyInsurance, String headImg, String grade, String jobTitle, String typeWork, String typeOfWork, String workingYears, String projectId, String belongForeman, String isRenewal, String status, String entisStatus, String isForeman) {
        this.id = id;
        this.name = name;
        this.isZjgongan = isZjgongan;
        this.idNumber = idNumber;
        this.nativePlace = nativePlace;
        this.sex = sex;
        this.nation = nation;
        this.mechanism = mechanism;
        this.idCardStartTime = idCardStartTime;
        this.idCardEndTime = idCardEndTime;
        this.idImg = idImg;
        this.idBackImg = idBackImg;
        this.sitePhotos = sitePhotos;
        this.phone = phone;
        this.age = age;
        this.workerNo = workerNo;
        this.companyId = companyId;
        this.froemanName = froemanName;
        this.companyName = companyName;
        this.projectName = projectName;
        this.politics = politics;
        this.educationType = educationType;
        this.marriage = marriage;
        this.bankName = bankName;
        this.bankCardNo = bankCardNo;
        this.unionPayAccount = unionPayAccount;
        this.joinGuildTime = joinGuildTime;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.attendanceNo = attendanceNo;
        this.majorDiseases = majorDiseases;
        this.joinGuild = joinGuild;
        this.isContract = isContract;
        this.shtick = shtick;
        this.isFree = isFree;
        this.isBuyInsurance = isBuyInsurance;
        this.headImg = headImg;
        this.grade = grade;
        this.jobTitle = jobTitle;
        this.typeWork = typeWork;
        this.typeOfWork = typeOfWork;
        this.workingYears = workingYears;
        this.projectId = projectId;
        this.belongForeman = belongForeman;
        this.isRenewal = isRenewal;
        this.status = status;
        this.entisStatus = entisStatus;
        this.isForeman = isForeman;
    }

    private String id;
    private String name;
    private String isZjgongan;
    private String idNumber;
    private String nativePlace;
    private String sex;
    private String nation;
    private String mechanism;
    private String idCardStartTime;
    private String idCardEndTime;
    private String idImg;
    private String idBackImg;
    private String sitePhotos;
    private String phone;
    private String age;
    private String workerNo;
    private String companyId;
    private String froemanName;
    private String companyName;
    private String projectName;
    private String politics;
    private String educationType;
    private String marriage;
    private String bankName;
    private String bankCardNo;
    private String unionPayAccount;
    private String joinGuildTime;
    private String emergencyContact;
    private String emergencyPhone;
    private String attendanceNo;
    private String majorDiseases;
    private String joinGuild;
    private String isContract;
    private String shtick;
    private String isFree;
    private String isBuyInsurance;
    private String headImg;
    private String grade;
    private String jobTitle;
    private String typeWork;
    private String typeOfWork;
    private String workingYears;
    private String projectId;
    private String belongForeman;
    private String isRenewal;
    private String status;
    private String entisStatus;
    private String isForeman;
    private String workerId;
    private String attendanceDate;
    private String type;
    private String clockTime;
    private String attendanceImg;
    private String desc;
    private String attendanceType;
    private String attendanceStatus;
    private String updownClock;
    private String teamId;
    private String attendanceAddres;


    public String getAttendanceImg() {
        return attendanceImg;
    }

    public void setAttendanceImg(String attendanceImg) {
        this.attendanceImg = attendanceImg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getUpdownClock() {
        return updownClock;
    }

    public void setUpdownClock(String updownClock) {
        this.updownClock = updownClock;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getAttendanceAddres() {
        return attendanceAddres;
    }

    public void setAttendanceAddres(String attendanceAddres) {
        this.attendanceAddres = attendanceAddres;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsZjgongan() {
        return isZjgongan;
    }

    public void setIsZjgongan(String isZjgongan) {
        this.isZjgongan = isZjgongan;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    public String getIdCardStartTime() {
        return idCardStartTime;
    }

    public void setIdCardStartTime(String idCardStartTime) {
        this.idCardStartTime = idCardStartTime;
    }

    public String getIdCardEndTime() {
        return idCardEndTime;
    }

    public void setIdCardEndTime(String idCardEndTime) {
        this.idCardEndTime = idCardEndTime;
    }

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

    public String getIdBackImg() {
        return idBackImg;
    }

    public void setIdBackImg(String idBackImg) {
        this.idBackImg = idBackImg;
    }

    public String getSitePhotos() {
        return sitePhotos;
    }

    public void setSitePhotos(String sitePhotos) {
        this.sitePhotos = sitePhotos;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWorkerNo() {
        return workerNo;
    }

    public void setWorkerNo(String workerNo) {
        this.workerNo = workerNo;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getFroemanName() {
        return froemanName;
    }

    public void setFroemanName(String froemanName) {
        this.froemanName = froemanName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPolitics() {
        return politics;
    }

    public void setPolitics(String politics) {
        this.politics = politics;
    }

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getUnionPayAccount() {
        return unionPayAccount;
    }

    public void setUnionPayAccount(String unionPayAccount) {
        this.unionPayAccount = unionPayAccount;
    }

    public String getJoinGuildTime() {
        return joinGuildTime;
    }

    public void setJoinGuildTime(String joinGuildTime) {
        this.joinGuildTime = joinGuildTime;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getAttendanceNo() {
        return attendanceNo;
    }

    public void setAttendanceNo(String attendanceNo) {
        this.attendanceNo = attendanceNo;
    }

    public String getMajorDiseases() {
        return majorDiseases;
    }

    public void setMajorDiseases(String majorDiseases) {
        this.majorDiseases = majorDiseases;
    }

    public String getJoinGuild() {
        return joinGuild;
    }

    public void setJoinGuild(String joinGuild) {
        this.joinGuild = joinGuild;
    }

    public String getIsContract() {
        return isContract;
    }

    public void setIsContract(String isContract) {
        this.isContract = isContract;
    }

    public String getShtick() {
        return shtick;
    }

    public void setShtick(String shtick) {
        this.shtick = shtick;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getIsBuyInsurance() {
        return isBuyInsurance;
    }

    public void setIsBuyInsurance(String isBuyInsurance) {
        this.isBuyInsurance = isBuyInsurance;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getTypeWork() {
        return typeWork;
    }

    public void setTypeWork(String typeWork) {
        this.typeWork = typeWork;
    }

    public String getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public String getWorkingYears() {
        return workingYears;
    }

    public void setWorkingYears(String workingYears) {
        this.workingYears = workingYears;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getBelongForeman() {
        return belongForeman;
    }

    public void setBelongForeman(String belongForeman) {
        this.belongForeman = belongForeman;
    }

    public String getIsRenewal() {
        return isRenewal;
    }

    public void setIsRenewal(String isRenewal) {
        this.isRenewal = isRenewal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntisStatus() {
        return entisStatus;
    }

    public void setEntisStatus(String entisStatus) {
        this.entisStatus = entisStatus;
    }

    public String getIsForeman() {
        return isForeman;
    }

    public void setIsForeman(String isForeman) {
        this.isForeman = isForeman;
    }
}

