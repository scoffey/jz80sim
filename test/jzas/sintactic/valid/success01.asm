		aseg
		org 100h


;en zona se ingresa un valor de 16 bits pudiendo ser este un rótulo, una constante o un registro de 16 bits
;reg puede ser cualquier registro a,b,c,d,h,l
descarga	macro zona, reg
		ld (backup), a
		ld a, reg
		ld (zona), a
		ld a, (backup)
		jr backup + 1
backup		ds 1
		endm



;reg = v - {a}
;zona = cualquier valor, registro o rotulo de 16 bits
carga		macro reg, zona
		ld (backup), a
		ld a, (zona)
		ld reg, a
		ld a, (backup)
		jr backup +1
backup		ds 1
		endm
		

inicio		ld a, 5
		descarga (150h), a
		carga b, dir
		rst 38h
dir		db 5
		end inicio
