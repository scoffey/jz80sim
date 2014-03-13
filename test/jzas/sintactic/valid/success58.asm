		aseg
		org 3000h

start0:		jp start

leeteclas:	ld (aux), BC
		ld (aux+2), HL 	; resguardo registros que reciben parametros
		pop BC
		ld (aux+4), BC	; resguardo dir. de instr. sig. a ret
		pop BC		; B = cant caracteres, C = basura
		pop HL		; HL = dir. de vuelco de caracteres leidos
		push AF		; resguardo AF en stack
esperatecla:	in A, (81h)
		cp 1
		jp NZ, esperatecla
		in A, (80h)
		ld (HL), A
		inc HL
		djnz esperatecla
		jr aux+6
aux:		ds 6
		pop AF		; restauro AF
		ld BC, (aux+4)
		push BC		; restauro dir. de instr. sig. a ret
		ld BC, (aux)
		ld HL, (aux+2)	; restauro registros que reciben parametros
		ret

start:		ld SP, 0
		ld BC, tecleado
		push BC
		ld B, cantTeclas
		ld C, 0
		push BC
		call leeteclas
		rst 38h
cantTeclas:	equ 10
tecleado:	ds cantTeclas
		end start0
