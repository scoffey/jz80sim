; Ejercicio 8 del TP 2.

		aseg
		org	1200h
start:		ld	A, 56h		                
		sub	A, 0CEh
		sub	A, 0A7h		 
		rst  	38h		        
		end  	start



