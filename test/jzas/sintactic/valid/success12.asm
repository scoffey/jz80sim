			aseg
			org 100h



;n1, n2 y mayor son direcciones de 16 bits tanto n1 como n2 no pueden ser bc pudiendo ser rotulos o registros al igualq ue mayor.

maximo			macro n1, n2, mayor
			local backup, carga
			ld (backup), a
			ld a, b
			ld (backup + 1), a
			ld a, (n1)
			ld b, a
			ld a, (n2)
			cp b
			jp p, carga
			ld a, (n1)
carga			ld (mayor), a
			ld a, (backup + 1)
			ld b, a
			ld a, (backup)
			jp backup + 2
backup			ds 2
			endm

inicio			ld ix, vector
			ld b, cardinalidad + 1
			ld a, -128
			ld (max), a
			jr chequeo
ciclo			maximo max, ix, max
			inc ix
chequeo			djnz ciclo
			rst 38h

vector			db 1,2,3,4,5,4,3,2,18,7
cardinalidad		equ $ - vector
max			ds 1
			end inicio