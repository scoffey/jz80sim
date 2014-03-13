		aseg
		org 100h
inicio		ld ix, vecfuente+cantelem-1
		ld iy, vecdestino
		ld b, cantelem
ciclo		ld a, (ix)
		ld (iy), a
		inc iy
		dec ix
		djnz ciclo
		rst 38h
vecfuente       db 10, -2, 8, -5, 6
cantelem	equ $-vecfuente
vecdestino	ds cantelem
		end inicio
