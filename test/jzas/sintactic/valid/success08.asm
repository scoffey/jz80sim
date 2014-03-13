                        aseg
			org 100h
inicio			ld de, string
			ld b, longitud+1
			jr chequeo
ciclo			ld a, (de)
			inc de
			cp letra
			jr z, encontrada
chequeo			djnz ciclo
encontrada		ld hl, string + longitud
                        dec hl
                        sbc hl, de
			ld (lugar), hl
			rst 38h
string			defm "Hola chau"
longitud		equ $-string
letra                   equ 'z'
lugar			ds 1
                        
                        end inicio
