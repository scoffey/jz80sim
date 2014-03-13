			aseg
			org 300h


;compara los valores de las direccionmes a las que apuntan ix e iy modificando los flags tras la operación
;
comparar		macro
			ld a, (backup)
			ld a, (ix)
			cp (iy)
			ld a, (backup)
			jp backup + 1
backup			ds 1
			endm

inicio			ld ix, numero
			ld iy, numero+cardinalidad
			ld b, cardinalidad/2
ciclo			comparar
			jp nz, fin
			inc ix
			dec iy
			djnz ciclo
			ld a, 1
			jp fin_ok
fin			ld a, 0
fin_ok			ld (respuesta), a
			rst 38h
numero			db 1,2,3,4,4,3,2,1
cardinalidad		equ $-numero
respuesta		db 15
			end inicio
			
			
