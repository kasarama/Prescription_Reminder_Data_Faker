CREATE OR REPLACE FUNCTION randomdate()
 RETURNS date
 LANGUAGE sql
AS $function$ 		
		select current_date - (select floor(random() * (select current_date-'1900-01-01')+1)::int);		
	$function$
;


CREATE OR REPLACE FUNCTION cpr(birth date, male boolean)
 RETURNS character
 LANGUAGE sql
AS $function$ 
		select concat(
			(select to_char(birth, 'DDMMYY')),
			(select (floor(random() * 10))),
			(select (floor(random() * 90)+10)::varchar),
			(select(floor(random() * 5))*2 + male::int)
		);
		
	$function$
;





CREATE OR REPLACE FUNCTION create_person()
 RETURNS TABLE(cpr character, first_name character varying, last_name character varying, gender character, birthday date, email text)
 LANGUAGE plpgsql
AS $function$ 
	declare 
	emails TEXT ARRAY  DEFAULT  ARRAY['debug000111@gmail.com', 'debug000222@gmail.com', 'debug000333@gmail.com'];
	begin
		return query
				with base as 
	(
		select randomDate() as birth, fn.first_name, fn.gender from first_name fn order by random() limit 1
	)
	
	select 
		cpr(
			base.birth,				
				(
				select case 
					when base.gender='M'
						then 1
						else 0
					end 
				 from base
				)::boolean 
			) as cpr, 	
		base.first_name as first_name, 
		(select ln2.last_name  from last_name ln2 order by random() limit 1) as last_name,
		base.gender as gender,  
		base.birth as birthday,
		emails[floor((random()*3 +1))::int] as email
	from base; 
	end	
	$function$
;




CREATE OR REPLACE FUNCTION populate_person(q integer)
 RETURNS text
 LANGUAGE plpgsql
AS $function$
	declare 
	counter int :=0;
	begin 		
		raise notice 'start with q: %', q ;		
		while counter < q loop 
			begin					
				insert into person select * from create_person ();			
				exception
					when unique_violation then 	 			
					raise notice 'Duplicate at counter: %', counter ;
					--counter := counter-1; 
					return populate_person(q-counter);
			end;
			counter := counter+1;		
		end loop;
		return true;							
	end
		$function$
;
