		aseg
		org 3000h

start0:		jp start

leer2num:	push BC
		push AF		; reguardo de registros
espera1:	in A, (81h)
		cp 0
		jr Z, espera1	; se espera hasta que se pulse la 1a. tecla
		in A, (80h)	; entonces se carga en A y ...
		cp '0'
		jp M, espera1
		cp '9'+1
		jp P, espera1	; ... si es una tecla numerica (sino sigue esperando)
		sub '0'		; se convierte a numero
		ld B, A		; antes de seguir se guarda en B
espera2:	in A, (81h)
		cp 0
		jr Z, espera2	; se espera hasta que se pulse la 2a. tecla
		in A, (80h)	; entonces se carga en A y ...
		cp '0'
		jp M, espera1
		cp '9'+1
		jp P, espera1	; ... si es una tecla numerica (sino recomienza todo)
		sub '0'		; se convierte a numero
		ld C, 10
		mlt BC		; se multiplica el primer numero por 10 y
		add A, C	; se suma al segundo para obtener el numero de 2 cifras en binario
		ld (IX), A	; finalmente se almacena en (IX) (parametro por registro)
		pop AF		; restauraciones
		pop BC
		ret

start:		ld SP,0
		call leer2num
		rst 38h
		end start0
