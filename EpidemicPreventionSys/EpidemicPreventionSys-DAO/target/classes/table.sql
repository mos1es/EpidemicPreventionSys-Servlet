drop table person;
create table person(
 id varchar2(32) primary key,
 name varchar2(32) not null,
 phone varchar2(11) not null ,
 idcard varchar2(18) not null,
 health varchar2(18),--'正常' '普通感冒' '呼吸2控难'
 Inflow varchar2(5),--是否外地流入
 flowaddress varchar2(188),--流入地址
 jnaddress varchar2 (100),--济南地址
 returndate date ,  --日期
 del int,               --数据是否删除
 tmperature number(3,1),--体温
 savepath varchar2(200) --图片的保存地址
 );
drop table locations;
create table locations(
    id int primary key ,
    name varchar2(32),
    pid int,
    type varchar2(20)

);

insert into locations values (1,'山东省',-1,'省');
insert into locations values (2,'山西省',-1,'省');
insert into locations values (3,'北京市',-1,'直辖市');
insert into locations values (4,'济南市',1,'市');
insert into locations values (5,'青岛市',1,'市');
insert into locations values (6,'天桥区',4,'区');
insert into locations values (7,'历下区',4,'区');
insert into locations values (8,'济阳县',4,'县');
insert into locations values (9,'市南区',5,'区');
insert into locations values (10,'市北区',5,'区');
insert into locations values (11,'太原市',2,'市');
insert into locations values (12,'大同市',2,'市');
insert into locations values (13,'万柏林区',11,'区');
insert into locations values (14,'杏花岭区',1,'区');
insert into locations values (15,'云岗区',12,'区');
insert into locations values (16,'平城区',12,'区');
insert into locations values (17,'北京市',3,'市');
insert into locations values (18,'西城区',17,'区');
insert into locations values (19,'东城区',17,'区');
commit;