select s.name, s.age, f.name
from student s
         left join faculty f on s.faculty_id = f.id
order by s.family, s.name;


select s.family, s.name
from student s
         inner join avatar a on s.id = a.student_id
where a.student_id is not null
