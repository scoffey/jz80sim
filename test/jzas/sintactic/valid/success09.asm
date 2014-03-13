		aseg
		org 100	
inicio		ld ix, vector
		ld iy, 0
ciclo		inc iy
		ld a, (ix)
		inc ix
		cp 0
		jp nz, ciclo
		dec iy
		rst 38h
vector		db 0,2,3,4,5,6,1,8,9
		end inicio
