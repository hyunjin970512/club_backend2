package kr.co.koreazinc.temp.repository.main;

import static kr.co.koreazinc.temp.model.entity.main.QCoEmplBas.coEmplBas;
import static kr.co.koreazinc.temp.model.entity.main.QMenuInfo.menuInfo;
import static kr.co.koreazinc.temp.model.entity.main.QMenuRoleMap.menuRoleMap;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.main.MenuInfo;

@Repository
@Transactional(readOnly = true)
public class MenuRepository extends AbstractJpaRepository<MenuInfo, Long> {

    public MenuRepository(List<EntityManager> entityManagers) {
        super(MenuInfo.class, entityManagers);
    }

    /** ✅ 체이닝용 래퍼 */
    public class SelectQuery<DTO> extends Query.Select<DTO> {

        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
        }

        public SelectQuery<DTO> eqEmpNo(String empNo) {
            query.where(coEmplBas.empNo.eq(empNo));
            return this;
        }

        public SelectQuery<DTO> onlyUseY() {
            query.where(menuInfo.useAt.eq("Y"), menuRoleMap.useAt.eq("Y"));
            return this;
        }

        public SelectQuery<DTO> orderDefault() {
            query.orderBy(menuInfo.sortOrder.asc(), menuInfo.menuId.asc());
            return this;
        }
    }

    /**
     * empNo로 메뉴 조회 (DTO는 Class<T>로 주입)
     */
    public <T> SelectQuery<T> selectMenusByEmpNo(Class<T> type) {
        return new SelectQuery<>(
            queryFactory
                .select(Projections.constructor(
                    type,
                    menuInfo.menuId,
                    menuInfo.menuName,
                    menuInfo.menuPath,
                    menuInfo.sortOrder
                ))
                .from(menuInfo)
                .join(menuRoleMap).on(menuInfo.menuId.eq(menuRoleMap.id.menuId))
                .join(coEmplBas).on(menuRoleMap.id.roleCd.eq(coEmplBas.emplRoleCd))
                .distinct()
        );
    }

    /** ✅ 엔티티 직접 조회용 */
    public SelectQuery<MenuInfo> selectQuery() {
        return new SelectQuery<>(queryFactory.selectFrom(menuInfo));
    }
}
