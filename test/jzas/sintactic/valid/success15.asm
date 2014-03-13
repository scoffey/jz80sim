			aseg
			org 100h
inicio			ld ix, cadena
			ld c, 0
ciclo			ld a, (ix) 			
			cp ' '
			jp nz, sigo
			inc c
sigo			inc ix
			cp 0
			jp nz, ciclo
			rst 38h					
cadena 			db ' ', 'a',' ', 'b',' ',  0, ' '
			end inicio
